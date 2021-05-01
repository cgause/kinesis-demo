package com.example.kinesisdemo;


import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import com.amazonaws.services.kinesis.producer.UserRecordResult;
import com.example.kinesisdemo.model.CpuStat;
import com.example.kinesisdemo.model.DiskStat;
import com.example.kinesisdemo.model.MemoryStat;
import com.example.kinesisdemo.model.MetricEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.nullness.qual.Nullable;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;

import java.nio.ByteBuffer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static oshi.hardware.CentralProcessor.TickType;

@Log4j2
public class MetricGenerator {
    private KinesisProducer kinesisProducer;
    private static final String STREAM_NAME = "host-metrics";
    private static final ObjectMapper MAPPER;
    private static final Executor RESULT_EXECUTOR;
    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        RESULT_EXECUTOR = Executors.newFixedThreadPool(3);
    }

    public static void main(String[] args) {
        final AtomicInteger count = new AtomicInteger();
        final SystemInfo systemInfo = new SystemInfo();
        MetricGenerator generator = new MetricGenerator();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    MetricEvent event = MetricEvent.builder()
                            .id(UUID.randomUUID().toString())
                            .hostName(String.format("hostname-%d", count.getAndIncrement() % 2))
                            .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                            .cpu(generator.getCpuStat(systemInfo.getHardware()))
                            .memory(generator.getMemoryStat(systemInfo.getHardware().getMemory()))
                            .disks(generator.getDiskStats(systemInfo.getOperatingSystem().getFileSystem().getFileStores()))
                            .build();
                    //System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(event));
                    generator.queueEventToKinesis(event);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 500);
    }

    private CpuStat getCpuStat(HardwareAbstractionLayer layer) throws InterruptedException {
        CentralProcessor processor = layer.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Thread.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();

        return CpuStat.builder()
                .cpuCount(processor.getLogicalProcessorCount())
                .idle(ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()])
                .system(ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()])
                .user(ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()])
                .build();
    }

    private MemoryStat getMemoryStat(GlobalMemory globalMemory) {
        return MemoryStat.builder()
                .availableMemory(globalMemory.getAvailable())
                .totalMemory(globalMemory.getTotal())
                .usedMemory(globalMemory.getTotal() - globalMemory.getAvailable())
                .build();
    }

    private List<DiskStat> getDiskStats(List<OSFileStore> fileStores) {
        return fileStores.stream()
                .map(fs -> DiskStat.builder()
                        .availableSpace(fs.getUsableSpace())
                        .diskUUID(fs.getUUID())
                        .diskLabel(fs.getLabel())
                        .totalSpace(fs.getTotalSpace()).build())
                .collect(Collectors.toList());
    }

    private void queueEventToKinesis(MetricEvent event) {
        if (kinesisProducer == null) {
            KinesisProducerConfiguration producerConfiguration =
                    new KinesisProducerConfiguration()
                            .setCredentialsProvider(new ProfileCredentialsProvider("kinesis-demo"))
                            .setAggregationEnabled(true)
                            .setAggregationMaxSize(500000)
                            .setRecordMaxBufferedTime(3000)
                            .setRegion("us-east-1");
            kinesisProducer = new KinesisProducer(producerConfiguration);
        }

        try {
            //process results async
            ListenableFuture<UserRecordResult> future =
                    kinesisProducer.addUserRecord(STREAM_NAME, event.getHostName(),
                            ByteBuffer.wrap(MAPPER.writeValueAsBytes(event)));
            Futures.addCallback(future, new FutureCallback<>() {
                @Override
                public void onSuccess(@Nullable UserRecordResult userRecordResult) {
                    log.info("record written to shard: " + userRecordResult.getShardId());
                }

                @Override
                public void onFailure(Throwable throwable) {
                    log.error("failed to write record {}", throwable);
                }
            }, RESULT_EXECUTOR);
        } catch (Exception e) {
            log.error("unable to send metric event {} for host {} {}", event.getId(), event.getHostName(), e);
        }
    }
}

package com.example.kinesisdemo.functions;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.example.kinesisdemo.model.MetricEvent;
import com.example.kinesisdemo.service.MetricService;
import com.example.kinesisdemo.util.RecordDeaggregator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

import java.util.function.Consumer;

@Component
@Log4j2
public class MetricEventConsumer implements Consumer<KinesisEvent> {

    private final MetricService metricService;
    private final ObjectMapper mapper;
    private final RecordDeaggregator<KinesisEventRecord> deaggregator = new RecordDeaggregator<>();
    public MetricEventConsumer(MetricService metricService, ObjectMapper mapper) {
        this.metricService = metricService;
        this.mapper = mapper;
    }

    @Override
    public void accept(KinesisEvent event) {
        try {
            for (KinesisClientRecord kcr : deaggregator.deaggregate(event.getRecords())) {
                byte[] data = new byte[kcr.data().remaining()];
                kcr.data().get(data);
                MetricEvent metric = mapper.readValue(data, MetricEvent.class);
                metricService.saveMetricEvent(metric);
                log.info("processed event with id: {}", metric.getId());
            }
        } catch (Exception e) {
            //TODO: toss record onto an error queue
            log.error("Error processing MetricEvent", e);
        }
    }
}

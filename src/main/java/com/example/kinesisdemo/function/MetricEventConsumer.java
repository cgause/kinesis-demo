package com.example.kinesisdemo.function;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.example.kinesisdemo.model.MetricEvent;
import com.example.kinesisdemo.service.MetricService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Consumer;

@Component
@Log4j2
public class MetricEventConsumer implements Consumer<KinesisEvent> {

    private final MetricService metricService;
    private final ObjectMapper mapper;
    public MetricEventConsumer(MetricService metricService, ObjectMapper mapper) {
        this.metricService = metricService;
        this.mapper = mapper;
    }

    @Override
    public void accept(KinesisEvent event) {
        for (KinesisEvent.KinesisEventRecord record : event.getRecords()) {
            try {
                log.info("data " + new String(record.getKinesis().getData().array()));
                MetricEvent metrics = mapper.readValue(record.getKinesis().getData().array(), MetricEvent.class);
                log.info(metrics.getId());
            } catch (IOException e) {
                log.error("Unable to parse json from byte array");
            }
        }
    }
}

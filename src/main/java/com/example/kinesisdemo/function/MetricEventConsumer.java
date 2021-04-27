package com.example.kinesisdemo.function;

import com.example.kinesisdemo.model.MetricEvent;
import com.example.kinesisdemo.service.MetricService;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Consumer;

@Component
public class MetricEventConsumer implements Consumer<Message<MetricEvent>> {

    private final MetricService metricService;

    public MetricEventConsumer(MetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    public void accept(Message<MetricEvent> metricEvent) {
        MetricEvent event = metricEvent.getPayload();
        try {
            metricService.saveMetricEvent(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

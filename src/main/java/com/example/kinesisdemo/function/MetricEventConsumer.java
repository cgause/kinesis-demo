package com.example.kinesisdemo.function;

import com.example.kinesisdemo.model.MetricEvent;
import com.example.kinesisdemo.service.MetricService;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Log4j2
public class MetricEventConsumer implements Consumer<Message<MetricEvent>> {

    private final MetricService metricService;

    public MetricEventConsumer(MetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    public void accept(Message<MetricEvent> metricEvent) {
        if (metricEvent == null) {
            log.info("No event provided");
            return;
        }

        MetricEvent event = metricEvent.getPayload();
        try {
            //metricService.saveMetricEvent(event);
            log.info("Received event id: {}", event.getId());
        } catch (Exception e) {
            log.error("unable to persist event with id {}", event.getId());
        }
    }
}

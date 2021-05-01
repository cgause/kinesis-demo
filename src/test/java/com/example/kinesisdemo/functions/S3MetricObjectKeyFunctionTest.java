package com.example.kinesisdemo.functions;

import com.example.kinesisdemo.model.MetricEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

public class S3MetricObjectKeyFunctionTest {

    @Test
    public void testKeyFunction() {
        MetricEvent event = MetricEvent.builder()
                .timestamp(ZonedDateTime.now())
                .id(UUID.randomUUID().toString())
                .hostName("unit-test").build();
        S3MetricObjectKeyFunction keyFunction = new S3MetricObjectKeyFunction();
        String key = keyFunction.apply(event);
        //expected key events/hostname/MMddyyyyHHmmssn/id.json
        String[] tokens = key.split("/");
        Assertions.assertEquals("events", tokens[0]);
        Assertions.assertEquals(event.getHostName(), tokens[1]);
        Assertions.assertEquals(event.getId() + ".json", tokens[3]);
    }

    @Test
    public void testKeyFunctionFailWithMissingElementsException() {
        MetricEvent event = MetricEvent.builder().id(UUID.randomUUID().toString()).build();
        Assertions.assertThrows(NullPointerException.class, () -> new S3MetricObjectKeyFunction().apply(event));
    }
}

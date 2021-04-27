package com.example.kinesisdemo;

import com.example.kinesisdemo.model.MetricEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MetricEventSerializerTest {

    @Test
    public void testUnmarshal() throws Exception {
        final String json = "{\"id\": \"123\", \"hostName\": \"spectre\", \"memory\": {\"total\": 16698454016, \"used\": 3745800192, \"free\": 4955889664, \"available\": 11480432640}, " +
                "\"cpu\": {\"count\": 4, \"user\": 3220.72, \"system\": 795.9, \"idle\": 19087.64}, " +
                "\"network\": {\"sent\": 9057498, \"received\": 362065811, \"dropped_in\": 0, \"dropped_out\": 0}}";

        final ObjectMapper MAPPER = new ObjectMapper();
        MetricEvent event = MAPPER.readValue(json, MetricEvent.class);
        Assertions.assertNotNull(event);
        System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(event));

        System.out.println(ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("MMddyyyyHHmmssSSSnz")));
    }
}

package com.example.kinesisdemo;

import com.example.kinesisdemo.model.MetricEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MetricEventSerializerTest {

    @Test
    public void testUnmarshal() throws Exception {
        final String json = "{\n" +
                "  \"id\" : \"33c34964-7f32-4fae-84e4-25e37183d1ae\",\n" +
                "  \"hostName\" : \"hostname-0\",\n" +
                "  \"timestamp\" : \"2021-04-28T20:07:24.657122+0000\",\n" +
                "  \"memory\" : {\n" +
                "    \"total\" : 16698454016,\n" +
                "    \"used\" : 6706647040,\n" +
                "    \"available\" : 9991806976\n" +
                "  },\n" +
                "  \"cpu\" : {\n" +
                "    \"user\" : 2130.0,\n" +
                "    \"system\" : 130.0,\n" +
                "    \"idle\" : 1760.0,\n" +
                "    \"count\" : 4\n" +
                "  },\n" +
                "  \"disks\" : [ {\n" +
                "    \"totalSpace\" : 49605709824,\n" +
                "    \"availableSpace\" : 25369124864,\n" +
                "    \"diskUUID\" : \"75621346-acfa-41a6-85f8-52fb3b6eb8d8\",\n" +
                "    \"diskLabel\" : \"\"\n" +
                "  }, {\n" +
                "    \"totalSpace\" : 117089861632,\n" +
                "    \"availableSpace\" : 38615531520,\n" +
                "    \"diskUUID\" : \"a30183a9-bf4b-4c6e-a825-0aae8063668d\",\n" +
                "    \"diskLabel\" : \"\"\n" +
                "  }, {\n" +
                "    \"totalSpace\" : 268435456,\n" +
                "    \"availableSpace\" : 150999040,\n" +
                "    \"diskUUID\" : \"9856-601d\",\n" +
                "    \"diskLabel\" : \"SYSTEM\"\n" +
                "  }, {\n" +
                "    \"totalSpace\" : 64072183808,\n" +
                "    \"availableSpace\" : 50223710208,\n" +
                "    \"diskUUID\" : \"1ed2bc3e18a2333f\",\n" +
                "    \"diskLabel\" : \"Music\"\n" +
                "  } ]\n" +
                "}";

        final ObjectMapper MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MetricEvent event = MAPPER.readValue(json, MetricEvent.class);
        Assertions.assertNotNull(event);
    }
}

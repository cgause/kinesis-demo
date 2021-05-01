package com.example.kinesisdemo.functions;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.example.kinesisdemo.model.MetricEvent;
import com.example.kinesisdemo.service.MetricService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MetricConsumerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    MetricService metricService;

    @BeforeAll
    public static void setUp() {
        SimpleModule module =  new SimpleModule();
        module.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(jsonParser.getValueAsLong());
                return calendar.getTime();
            }
        });

        MAPPER.registerModule(module);
        MAPPER.registerModule(new JodaModule());
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    @Test
    public void testMetricConsumer() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/kinesis-event.json");
        KinesisEvent event = MAPPER.readValue(inputStream, KinesisEvent.class);
        MetricEventConsumer consumer = new MetricEventConsumer(metricService, MAPPER);
        ArgumentCaptor<MetricEvent> metricEventArgumentCaptor = ArgumentCaptor.forClass(MetricEvent.class);
        consumer.accept(event);
        verify(metricService, times(4)).saveMetricEvent(metricEventArgumentCaptor.capture());
        Assertions.assertEquals(4, metricEventArgumentCaptor.getAllValues().size());
    }
}

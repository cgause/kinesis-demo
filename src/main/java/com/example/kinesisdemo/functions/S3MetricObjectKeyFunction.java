package com.example.kinesisdemo.functions;

import com.example.kinesisdemo.model.MetricEvent;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Function;

public class S3MetricObjectKeyFunction implements Function< MetricEvent, String> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyyHHmmssn");
    /**
     * Generates the object key to use for committing a metric event to S3
     * @param event
     * @return String representing the object key
     */
    @Override
    public String apply(MetricEvent event) {
        Objects.requireNonNull(event, "event is required");
        Objects.requireNonNull(event.getHostName(), "event is missing hostname");
        Objects.requireNonNull(event.getId(), "event is missing id");
        Objects.requireNonNull(event.getTimestamp(), "event is missing timestamp");

        return String.format("events/%s/%s/%s.json",
                event.getHostName(), event.getTimestamp().format(DATE_FORMATTER), event.getId());
    }
}

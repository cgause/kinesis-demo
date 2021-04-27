package com.example.kinesisdemo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.kinesisdemo.model.MetricEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class S3MetricService implements MetricService {

    private static final String BUCKET_NAME = "com-rhymeswithsauce-metrics";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyyHHmmssSSSn");
    private final AmazonS3 s3Client;
    private final ObjectMapper objectMapper;

    public S3MetricService(AmazonS3 s3Client, ObjectMapper objectMapper) {
        this.s3Client = s3Client;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveMetricEvent(MetricEvent event) throws IOException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        String key = String.format("%s/%s", event.getHostName(), now.format(DATE_FORMATTER));
        s3Client.putObject(BUCKET_NAME, key, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(event));
    }
}

package com.example.kinesisdemo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.kinesisdemo.functions.S3MetricObjectKeyFunction;
import com.example.kinesisdemo.model.MetricEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Function;

@Service
@Log4j2
public class S3MetricService implements MetricService {

    private static final String BUCKET_NAME = "com-rhymeswithsauce-metrics";
    private static final Function<MetricEvent, String> KEY_FUNCTION = new S3MetricObjectKeyFunction();
    private final AmazonS3 s3Client;
    private final ObjectMapper objectMapper;

    public S3MetricService(AmazonS3 s3Client, ObjectMapper objectMapper) {
        this.s3Client = s3Client;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveMetricEvent(MetricEvent event) throws IOException {
        s3Client.putObject(BUCKET_NAME, KEY_FUNCTION.apply(event), objectMapper.writeValueAsString(event));
    }
}

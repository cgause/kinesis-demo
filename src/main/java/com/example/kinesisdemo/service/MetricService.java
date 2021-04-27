package com.example.kinesisdemo.service;

import com.example.kinesisdemo.model.MetricEvent;

import java.io.IOException;

public interface MetricService {

    void saveMetricEvent(MetricEvent event) throws IOException;
}

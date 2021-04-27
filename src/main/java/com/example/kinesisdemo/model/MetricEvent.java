package com.example.kinesisdemo.model;

import lombok.Data;

@Data
public class MetricEvent {

    private String id;
    private String hostName;
    private MemoryStat memory;
    private CpuStat cpu;
    private NetworkStat network;
}

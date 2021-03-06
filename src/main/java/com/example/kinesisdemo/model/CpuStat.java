package com.example.kinesisdemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CpuStat {

    @JsonProperty("count")
    private int cpuCount;
    private double user;
    private double system;
    private double idle;
}

package com.example.kinesisdemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoryStat {
    @JsonProperty("total")
    private long totalMemory;
    @JsonProperty("used")
    private long usedMemory;
    @JsonProperty("free")
    private long freeMemory;
    @JsonProperty("available")
    private long availableMemory;
}

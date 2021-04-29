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
public class MemoryStat {
    @JsonProperty("total")
    private long totalMemory;
    @JsonProperty("used")
    private long usedMemory;
    @JsonProperty("available")
    private long availableMemory;
}

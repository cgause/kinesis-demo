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
public class DiskStat {

    private long totalSpace;
    private long availableSpace;
    private String diskUUID;
    private String diskLabel;
}

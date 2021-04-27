package com.example.kinesisdemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkStat {

    @JsonProperty("sent")
    private long bytesSent;
    @JsonProperty("received")
    private long bytesReceived;
    @JsonProperty("dropped_in")
    private long droppedIn;
    @JsonProperty("dropped_out")
    private long droppedOut;
}

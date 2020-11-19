package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWDeviceChannelDetailsV1 implements Serializable {
    public final String message;
    public final List<PxMWDeviceChannelDetailV1> data;

    @JsonCreator
    public PxMWDeviceChannelDetailsV1(@JsonProperty("message") String message,
            @JsonProperty("data") List<PxMWDeviceChannelDetailV1> data) {
        this.message = message;
        this.data = data;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("data")
    public List<PxMWDeviceChannelDetailV1> getData() {
        return data;
    }
}

package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWDeviceChannelDetailV1 implements Serializable {
    public final String deviceGuid;
    public final List<PxMWChannelDataV1> channelData;

    @JsonCreator
    public PxMWDeviceChannelDetailV1(@JsonProperty("id") String id,
            @JsonProperty("channelData") List<PxMWChannelDataV1> channelData) {
        this.deviceGuid = id;
        this.channelData = channelData;
    }

    @JsonProperty("id")
    public String getDeviceGuid() {
        return deviceGuid;
    }

    @JsonProperty("channelData")
    public List<PxMWChannelDataV1> getChannelData() {
        return channelData;
    }
}

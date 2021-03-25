package com.cannontech.dr.pxmw.model.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesDeviceV1 {
    public String deviceId;
    public String tagTrait;

    @JsonCreator
    public PxMWTimeSeriesDeviceV1(@JsonProperty("device_id") String deviceId, @JsonProperty("tag_trait") String tagTrait) {
        this.deviceId = deviceId;
        this.tagTrait = tagTrait;
    }

    @JsonProperty("device_id")
    public String getDeviceId() {
        return deviceId;
    }

    @JsonProperty("tag_trait")
    public String getTagTrait() {
        return tagTrait;
    }
}

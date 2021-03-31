package com.cannontech.dr.pxmw.model.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesDeviceV1 {
    private String deviceGuid;
    private String tagTrait;

    @JsonCreator
    public PxMWTimeSeriesDeviceV1(@JsonProperty("device_id") String deviceGuid, @JsonProperty("tag_trait") String tagTrait) {
        this.deviceGuid = deviceGuid;
        this.tagTrait = tagTrait;
    }

    @JsonProperty("device_id")
    public String getDeviceGuid() {
        return deviceGuid;
    }

    @JsonProperty("tag_trait")
    public String getTagTrait() {
        return tagTrait;
    }
}

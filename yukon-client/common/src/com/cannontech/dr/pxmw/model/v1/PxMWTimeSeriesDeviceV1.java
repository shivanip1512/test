package com.cannontech.dr.pxmw.model.v1;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesDeviceV1 {
    private String deviceGuid;
    private List<String> tagTrait;

    @JsonCreator
    public PxMWTimeSeriesDeviceV1(@JsonProperty("device_id") String deviceGuid, @JsonProperty("tag_trait") List<String> tagTrait) {
        this.deviceGuid = deviceGuid;
        this.tagTrait = tagTrait;
    }

    @JsonProperty("device_id")
    public String getDeviceGuid() {
        return deviceGuid;
    }

    @JsonProperty("tag_trait")
    public List<String> getTagTrait() {
        return tagTrait;
    }
}

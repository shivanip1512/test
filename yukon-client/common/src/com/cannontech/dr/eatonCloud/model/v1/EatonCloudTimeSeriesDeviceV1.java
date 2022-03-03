package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudTimeSeriesDeviceV1 implements Serializable{
    private String deviceGuid;
    private String tagTrait;

    @JsonCreator
    public EatonCloudTimeSeriesDeviceV1(@JsonProperty("device_id") String deviceGuid, @JsonProperty("tag_trait") String tagTrait) {
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

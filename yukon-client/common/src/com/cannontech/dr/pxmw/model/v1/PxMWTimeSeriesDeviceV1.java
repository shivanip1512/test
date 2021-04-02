package com.cannontech.dr.pxmw.model.v1;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWTimeSeriesDeviceV1 {
    private String deviceGuid;
    private String tagTrait;

    @JsonCreator
    public PxMWTimeSeriesDeviceV1(@JsonProperty("device_id") String deviceGuid, @JsonProperty("tag_trait") List<String> tagTraitList) {
        this.deviceGuid = deviceGuid;
        this.tagTrait = String.join(",", tagTraitList);
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

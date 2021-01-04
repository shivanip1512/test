package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWChannelValueV1 implements Serializable {
    private final String tag;
    private final String value;

    @JsonCreator
    public PxMWChannelValueV1(@JsonProperty("tag") String tag, @JsonProperty("value") String value) {
        this.tag = tag;
        this.value = value;
    }

    @JsonProperty("tag")
    public String getTag() {
        return tag;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }
}

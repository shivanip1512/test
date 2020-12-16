package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWChannelValuesRequestV1 implements Serializable {
    private final List<String> tags;

    @JsonCreator
    public PxMWChannelValuesRequestV1(@JsonProperty("tags") List<String> tags) {
        this.tags = tags;
    }

    @JsonProperty("tags")
    public List<String> getTags() {
        return tags;
    }
}

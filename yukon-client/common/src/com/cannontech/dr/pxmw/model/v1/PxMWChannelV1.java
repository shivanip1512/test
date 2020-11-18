package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWChannelV1 implements Serializable {
    private final String tag;
    private final String mcltag;

    @JsonCreator
    public PxMWChannelV1(@JsonProperty("tag") String tag, @JsonProperty("mcltag") String mcltag) {
        this.tag = tag;
        this.mcltag = mcltag;
    }

    @JsonProperty("tag")
    public String getTag() {
        return tag;
    }

    @JsonProperty("mcltag")
    public String getMcltag() {
        return mcltag;
    }
}

package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWChannelDataV1 implements Serializable {
    private final String tag;
    private final String time;
    private final String value;
    private final String nvalue;
    @JsonCreator
    public PxMWChannelDataV1(@JsonProperty("tag") String tag, @JsonProperty("time") String time,
            @JsonProperty("value") String value,  @JsonProperty("nvalue") String nvalue) {
        this.tag = tag;
        this.time = time;
        this.value = value;
        this.nvalue = nvalue;
    }
    
    @JsonProperty("tag")
    public String getTag() {
        return tag;
    }

    @JsonProperty("time")
    public String getTime() {
        return time;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }
    
    @JsonProperty("nvalue")
    public String getNvalue() {
        return nvalue;
    }
}

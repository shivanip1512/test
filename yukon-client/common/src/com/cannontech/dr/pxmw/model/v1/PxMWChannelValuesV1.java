package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWChannelValuesV1 implements Serializable {
    private final String status;
    private final String msg;
    private final List<PxMWChannelValueV1> values;

    @JsonCreator
    public PxMWChannelValuesV1(@JsonProperty("status") String status, @JsonProperty("msg") String msg,
            @JsonProperty("params") List<PxMWChannelValueV1> values) {
        this.status = status;
        this.msg = msg;
        this.values = values;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("msg")
    public String getMsg() {
        return msg;
    }
    
    @JsonProperty("params")
    public List<PxMWChannelValueV1> getValues() {
        return values;
    }
}

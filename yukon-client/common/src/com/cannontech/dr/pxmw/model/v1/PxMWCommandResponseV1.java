package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PxMWCommandResponseV1 implements Serializable {
    private final Integer status;
    private final String msg;

    @JsonCreator
    public PxMWCommandResponseV1(@JsonProperty("status") Integer status, @JsonProperty("msg") String msg) {
        this.status = status;
        this.msg = msg;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("msg")
    public String getMsg() {
        return msg;
    }
}

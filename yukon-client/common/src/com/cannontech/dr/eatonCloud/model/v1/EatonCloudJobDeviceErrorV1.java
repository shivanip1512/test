package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudJobDeviceErrorV1 implements Serializable {
    private final Integer status;
    private final Integer errorNumber;

    @JsonCreator
    public EatonCloudJobDeviceErrorV1(@JsonProperty("status") Integer status, @JsonProperty("errorNumber") Integer errorNumber) {
        this.status = status;
        this.errorNumber = errorNumber;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("errorNumber")
    public Integer getErrorNumber() {
        return errorNumber;
    }
}

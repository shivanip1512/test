package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudCommandResponseV1 implements Serializable {
    private final Integer statusCode;
    private final String message;

    @JsonCreator
    public EatonCloudCommandResponseV1(@JsonProperty("statusCode") Integer statusCode, @JsonProperty("message") String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @JsonProperty("statusCode")
    public Integer getStatusCode() {
        return statusCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
}

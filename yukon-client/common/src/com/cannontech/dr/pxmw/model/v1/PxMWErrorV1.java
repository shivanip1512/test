package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWErrorV1 implements Serializable {
    private String message;
    private String description;

    @JsonCreator
    public PxMWErrorV1(@JsonProperty("message") String message, @JsonProperty("description") String description) {
        this.message = message;
        this.description = description;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }
}

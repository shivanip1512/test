package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWErrorsV1 implements Serializable{
    public List<PxMWErrorV1> errors;

    @JsonCreator
    public PxMWErrorsV1(@JsonProperty("errors") List<PxMWErrorV1> errors) {
        this.errors = errors;
    }

    @JsonProperty("errors")
    public List<PxMWErrorV1> getErrors() {
        return errors;
    }
}

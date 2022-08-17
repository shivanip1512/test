package com.cannontech.rest.api.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MockApiGlobalError {

    private String code;

    public MockApiGlobalError(String code) {
        super();
        this.code = code;
    }
    
    @JsonCreator
    public MockApiGlobalError() {
    }
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

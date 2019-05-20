package com.cannontech.web.api.errorHandler.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApiGlobalError {

    private String code;

    public ApiGlobalError(String code) {
        super();
        this.code = code;
    }
    
    @JsonCreator
    public ApiGlobalError() {
    }
    
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

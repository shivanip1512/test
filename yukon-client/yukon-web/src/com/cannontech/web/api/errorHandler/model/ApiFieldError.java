package com.cannontech.web.api.errorHandler.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApiFieldError {

    private String field;
    private String code;
    @JsonInclude(Include.NON_NULL)
    private Object rejectedValue;
    
    public ApiFieldError(String field, String code, Object rejectedValue) {
        super();
        this.field = field;
        this.code = code;
        this.rejectedValue = rejectedValue;
    }
    
    @JsonCreator
    public ApiFieldError() {
    }
    
    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public Object getRejectedValue() {
        return rejectedValue;
    }
    
    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }
}

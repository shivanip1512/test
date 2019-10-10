package com.cannontech.rest.api.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class MockApiError {

    private int status;
    private String message;
    
    @JsonInclude(Include.NON_NULL)
    private String errorCode;

    @JsonInclude(Include.NON_NULL)
    private List<String> errors;
    
    @JsonInclude(Include.NON_NULL)
    private List<MockApiFieldError> fieldErrors;
    
    @JsonInclude(Include.NON_NULL)
    private List<MockApiGlobalError> globalErrors;
    
    @JsonCreator
    public MockApiError(){
        
    }

    public MockApiError(int status, String message, List<MockApiFieldError> fieldErrors, List<MockApiGlobalError> globalErrors, String errorCode) {
        this.status = status;
        this.message = message;
        this.fieldErrors = fieldErrors;
        this.globalErrors =globalErrors;
        this.errorCode = errorCode;
    }

    public MockApiError(int status, String message, String errorCode) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }

    public MockApiError(int status, String message, final List<String> errors, String errorCode) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.errorCode = errorCode;
    }
    

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<MockApiFieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<MockApiFieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public List<MockApiGlobalError> getGlobalErrors() {
        return globalErrors;
    }

    public void setGlobalErrors(List<MockApiGlobalError> globalErrors) {
        this.globalErrors = globalErrors;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public String getErrorCode() {
        return errorCode;
    }

}

package com.cannontech.web.api.errorHandler.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ApiError {

    private int status;
    private String message;
    
    @JsonInclude(Include.NON_NULL)
    private String errorCode;

    @JsonInclude(Include.NON_NULL)
    private List<String> errors;
    
    @JsonInclude(Include.NON_NULL)
    private List<ApiFieldError> fieldErrors;
    
    @JsonInclude(Include.NON_NULL)
    private List<ApiGlobalError> globalErrors;
    
    @JsonCreator
    public ApiError(){
        
    }

    public ApiError(int status, String message, List<ApiFieldError> fieldErrors, List<ApiGlobalError> globalErrors, String errorCode) {
        this.status = status;
        this.message = message;
        this.fieldErrors = fieldErrors;
        this.globalErrors =globalErrors;
        this.errorCode = errorCode;
    }

    public ApiError(int status, String message, String errorCode) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
    }

    public ApiError(int status, String message, final List<String> errors, String errorCode) {
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

    public List<ApiFieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<ApiFieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public List<ApiGlobalError> getGlobalErrors() {
        return globalErrors;
    }

    public void setGlobalErrors(List<ApiGlobalError> globalErrors) {
        this.globalErrors = globalErrors;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public String getErrorCode() {
        return errorCode;
    }

}

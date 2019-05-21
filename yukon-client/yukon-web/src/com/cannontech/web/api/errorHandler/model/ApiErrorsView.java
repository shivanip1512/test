package com.cannontech.web.api.errorHandler.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApiErrorsView {

    private List<ApiFieldError> fieldErrors;
    private List<ApiGlobalError> globalErrors;
    
    public ApiErrorsView(List<ApiFieldError> fieldErrors, List<ApiGlobalError> globalErrors) {
        super();
        this.fieldErrors = fieldErrors;
        this.globalErrors = globalErrors;
    }
    
    @JsonCreator
    public ApiErrorsView() {
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
    
}

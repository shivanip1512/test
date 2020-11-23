package com.cannontech.web.api.error.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/*
 * Model for API error response
 */
public class ApiErrorModel {
    String title;
    String type;
    int code;
    String detail;
    String requestUri;
    String logRef;
    // Only include for validation errors.
    @JsonInclude(Include.NON_NULL) List<ApiFieldErrorModel> errors;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getLogRef() {
        return logRef;
    }

    public void setLogRef(String logRef) {
        this.logRef = logRef;
    }

    public List<ApiFieldErrorModel> getErrors() {
        return errors;
    }

    public void setErrors(List<ApiFieldErrorModel> errors) {
        this.errors = errors;
    }

}

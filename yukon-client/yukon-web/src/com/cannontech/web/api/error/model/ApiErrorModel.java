package com.cannontech.web.api.error.model;

import java.util.List;

import com.cannontech.api.error.model.ApiErrorCategory;
import com.cannontech.api.error.model.ApiErrorDetails;
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
    
    public ApiErrorModel() {
    }

    public ApiErrorModel(ApiErrorCategory errorCategory, String requestUri, String logRef) {
        super();
        this.title = errorCategory.getTitle();
        this.type = errorCategory.getType();
        this.code = errorCategory.getCode();
        this.detail = errorCategory.getDefaultMessage();
        this.requestUri = requestUri;
        this.logRef = logRef;
    }

    public ApiErrorModel(ApiErrorDetails errorDetails, String requestUri, String logRef) {
        this(errorDetails, errorDetails.getDefaultMessage(), requestUri, logRef);
    }

    public ApiErrorModel(ApiErrorDetails errorDetails, String detail, String requestUri, String logRef) {
        this.title = errorDetails.getTitle();
        this.type = errorDetails.getType();
        this.code = errorDetails.getCode();
        this.detail = detail;
        this.requestUri = requestUri;
        this.logRef = logRef;
    }

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

    @Override
    public String toString() {
        return "ApiErrorModel [title=" + title + ", type=" + type + ", code=" + code + ", detail=" + detail + ", requestUri="
                + requestUri + ", logRef=" + logRef + ", errors=" + errors + "]";
    }

}

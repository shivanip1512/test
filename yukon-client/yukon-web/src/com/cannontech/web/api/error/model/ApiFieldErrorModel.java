package com.cannontech.web.api.error.model;

import org.springframework.validation.FieldError;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ApiFieldErrorModel {
    String title;
    String type;
    int code;
    String detail;
    String field;
    String rejectedValue;
    @JsonInclude(Include.NON_NULL)
    Object[] parameters;

    public ApiFieldErrorModel() {
    }

    public ApiFieldErrorModel(ApiErrorDetails errorDetails, FieldError fieldError) {
        this.title = errorDetails.getTitle();
        this.type = errorDetails.getType();
        this.code = errorDetails.getCode();
        this.detail = errorDetails.getDefaultMessage();
        this.field = fieldError.getField();
        this.rejectedValue = String.valueOf(fieldError.getRejectedValue());
        this.parameters = fieldError.getArguments();
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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(String rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

}
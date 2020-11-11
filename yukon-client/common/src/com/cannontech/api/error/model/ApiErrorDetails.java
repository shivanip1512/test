package com.cannontech.api.error.model;

/*
 * Details of API errors.
 */
public enum ApiErrorDetails {
    // Global exceptions
    OBJECT_ALREADY_EXISTS(0, 102001, "Object Already Exists", "An identical object already exists."),

    // Validation Errors
    VALIDATION_FAILED(0, 101000, "Validation Failed", "Validation failed: see fields for details."),
    INVALID_VALUE(101000, 101003, "Invalid Value", "Invalid value for the field."),
    MAX_LENGTH_EXCEEDED(101000, 101004, "Max Length Exceeded", "Max length exceeded.");

    private int parentCode;
    private int code;
    private String title;
    private String defaultMessage;

    ApiErrorDetails(int parentCode, int code, String title, String defaultMessage) {
        this.parentCode = parentCode;
        this.code = code;
        this.title = title;
        this.defaultMessage = defaultMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public int getParentCode() {
        return parentCode;
    }

    public void setParentCode(int parentCode) {
        this.parentCode = parentCode;
    }

    // This will return the type, url is fixed, with code appended
    public String getType() {
        return "/api/errors/" + getCode();
    }

}

package com.cannontech.api.error.model;

/*
 * Details of API errors.
 */
public enum ApiErrorDetails {
    // Exceptions
    OBJECT_ALREADY_EXISTS(ApiErrorCategory.NONE.getCode() + 101, ApiErrorCategory.NONE, "Object Already Exists",
            "An identical object already exists."),

    // Validation Errors
    INVALID_VALUE(ApiErrorCategory.VALIDATION_FAILED.getCode() + 101, ApiErrorCategory.VALIDATION_FAILED, "Invalid Value",
            "Invalid value for the field."),
    MAX_LENGTH_EXCEEDED(ApiErrorCategory.VALIDATION_FAILED.getCode() + 102, ApiErrorCategory.VALIDATION_FAILED,
            "Max Length Exceeded", "Max length exceeded.");

    private ApiErrorCategory category;
    private int code;
    private String title;
    private String defaultMessage;

    ApiErrorDetails(int code, ApiErrorCategory category, String title, String defaultMessage) {
        this.code = code;
        this.category = category;
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

    public ApiErrorCategory getCategory() {
        return category;
    }

    public void setCategory(ApiErrorCategory category) {
        this.category = category;
    }

    // This will return the type, url is fixed, with code appended
    public String getType() {
        return "/api/errors/" + getCode();
    }

}

package com.cannontech.api.error.model;

public enum ApiErrorCategory {
    NONE(100000, "None", "None"),
    VALIDATION_FAILED(101000, "Validation Failed", "Validation failed: see fields for details.");

    private int code;
    private String title;
    private String defaultMessage;

    ApiErrorCategory(int code, String title, String defaultMessage) {
        this.code = code;
        this.title = title;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    // This will return the type, url is fixed, with code appended
    public String getType() {
        return "/api/errors/" + getCode();
    }
}

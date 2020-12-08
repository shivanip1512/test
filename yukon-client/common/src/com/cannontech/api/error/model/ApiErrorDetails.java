package com.cannontech.api.error.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/*
 * Details of API errors.
 */
public enum ApiErrorDetails {
    // Exceptions
    OBJECT_ALREADY_EXISTS(ApiErrorCategory.NONE.getCode() + 101, ApiErrorCategory.NONE, "Object Already Exists",
            "An identical object already exists."),
    AUTHENTICATION_INVALID(ApiErrorCategory.NONE.getCode() + 102, ApiErrorCategory.NONE, "Authentication Invalid",
            "Username or Password not valid."),
    NOT_AUTHORIZED(ApiErrorCategory.NONE.getCode() + 103, ApiErrorCategory.NONE, "Not Authorized",
            "User not authorized to perform this transaction."),
    DATABASE_ERROR(ApiErrorCategory.NONE.getCode() + 104, ApiErrorCategory.NONE, "Database Error",
            "Database error occured while performing this transaction."),
    METHOD_ARGUMENT_MISMATCH(ApiErrorCategory.NONE.getCode() + 105, ApiErrorCategory.NONE, "Method Argument Mismatch",
            "Method arguments are not matching."),
    BAD_REQUEST(ApiErrorCategory.NONE.getCode() + 106, ApiErrorCategory.NONE, "Bad Request", "Bad Request."),

    // Validation Errors
    INVALID_VALUE(ApiErrorCategory.VALIDATION_FAILED.getCode() + 101, ApiErrorCategory.VALIDATION_FAILED, "Invalid Value",
            "Invalid value for the field."),
    FIELD_REQUIRED(ApiErrorCategory.VALIDATION_FAILED.getCode() + 102, ApiErrorCategory.VALIDATION_FAILED, "Required field",
            "Field is required."),
    MAX_LENGTH_EXCEEDED(ApiErrorCategory.VALIDATION_FAILED.getCode() + 103, ApiErrorCategory.VALIDATION_FAILED,
            "Max Length Exceeded", "Max length exceeded."),
    ILLEGAL_CHARACTERS(ApiErrorCategory.VALIDATION_FAILED.getCode() + 104, ApiErrorCategory.VALIDATION_FAILED,
            "Illegal Characters", "Invalid characters."),
    VALUE_OUTSIDE_VALID_RANGE(ApiErrorCategory.VALIDATION_FAILED.getCode() + 105, ApiErrorCategory.VALIDATION_FAILED,
            "Invalid Range", "Provided value is out of range."),
    ALREADY_EXISTS(ApiErrorCategory.VALIDATION_FAILED.getCode() + 106, ApiErrorCategory.VALIDATION_FAILED, "Already exists",
            "Already exists."),
    DOES_NOT_EXISTS(ApiErrorCategory.VALIDATION_FAILED.getCode() + 107, ApiErrorCategory.VALIDATION_FAILED, "Does not exists",
            "Does not exists."),
    NOT_SUPPORTED(ApiErrorCategory.VALIDATION_FAILED.getCode() + 108, ApiErrorCategory.VALIDATION_FAILED, "Not Supported",
            "Provided type not supported."),
    PAST_DATE(ApiErrorCategory.VALIDATION_FAILED.getCode() + 109, ApiErrorCategory.VALIDATION_FAILED, "Past Date",
            "Date must be in the past.");

    private ApiErrorCategory category;
    private int code;
    private String title;
    private String defaultMessage;

    private static final Map<Integer, ApiErrorDetails> apiErrorDetails;

    static {
        ImmutableMap.Builder<Integer, ApiErrorDetails> validationFieldErrorsBuilder = ImmutableMap.builder();
        for (ApiErrorDetails errorDetails : ApiErrorDetails.values()) {
                validationFieldErrorsBuilder.put(errorDetails.code, errorDetails);
        }
        apiErrorDetails = validationFieldErrorsBuilder.build();
    }

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

    /**
     * Returns ApiErrorDetails for the specified code.
     */
    public static ApiErrorDetails getError(String code) {
        return apiErrorDetails.get(Integer.valueOf(code));
    }

}
package com.cannontech.api.error.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/*
 * Details of API errors.
 */
public enum ApiErrorDetails {
    // Exceptions
    OBJECT_ALREADY_EXISTS(101, "Object Already Exists", "An identical object already exists."), // code : 100101
    AUTHENTICATION_INVALID(102, "Authentication Invalid", "Username or Password not valid."), // code : 100102
    NOT_AUTHORIZED(103, "Not Authorized", "User not authorized to perform this transaction."), // code : 100103
    DATABASE_ERROR(104, "Database Error", "Database error occured while performing this transaction."), // code : 100104
    METHOD_ARGUMENT_MISMATCH(105, "Method Argument Mismatch", "Method arguments are not matching."), // code : 100105
    BAD_REQUEST(106, "Bad Request", "Bad Request."), // code : 100106

    // Validation Errors
    INVALID_VALUE(ApiErrorCategory.VALIDATION_FAILED, 101, "Invalid Value", "Invalid value for the field."), // code : 101101
    FIELD_REQUIRED(ApiErrorCategory.VALIDATION_FAILED, 102, "Required field", "Field is required."), // code : 101102
    MAX_LENGTH_EXCEEDED(ApiErrorCategory.VALIDATION_FAILED, 103, "Max Length Exceeded", "Max length exceeded."), // code : 101103
    ILLEGAL_CHARACTERS(ApiErrorCategory.VALIDATION_FAILED, 104, "Illegal Characters", "Invalid characters."), // code : 101104
    VALUE_OUTSIDE_VALID_RANGE(ApiErrorCategory.VALIDATION_FAILED, 105, "Invalid Range", "Provided value is out of range."), // code : 101105
    ALREADY_EXISTS(ApiErrorCategory.VALIDATION_FAILED, 106, "Already exists", "Already exists."), // code : 101106
    DOES_NOT_EXISTS(ApiErrorCategory.VALIDATION_FAILED, 107, "Does not exists", "Does not exists."), // code : 101107
    NOT_SUPPORTED(ApiErrorCategory.VALIDATION_FAILED, 108, "Not Supported", "Provided type not supported."), // code : 101108
    START_DATE_BEFORE_END_DATE(ApiErrorCategory.VALIDATION_FAILED, 109, "Start date must me before end date", "Start date must me before end date."),

    NOT_GREATER_THAN_INT(ApiErrorCategory.VALIDATION_FAILED, 110, "Not more than Integer", "Not more than Integer."), // code : 101110
    INVALID_STRING_LENGTH(ApiErrorCategory.VALIDATION_FAILED, 111, "Invalid String Length", "Invalid String Length."), // code : 101111
    DATA_ARCHIVING_INTERVEL_TOO_LARGE(ApiErrorCategory.VALIDATION_FAILED, 112, "Data archiving Intervel is too large", "Data archiving intervel is too large."), // code : 101112
    IS_NOT_POSITIVE(ApiErrorCategory.VALIDATION_FAILED, 113, "Must be a positive number", "Must be a positive number."), // code : 101113
    POINT_OFFSET_NOT_AVAILABLE(ApiErrorCategory.VALIDATION_FAILED, 114, "Point offset not available", "Point offset not availavble."), // code : 101114
    POINT_TYPE_MISMATCH(ApiErrorCategory.VALIDATION_FAILED, 115, "Point type mismatch", "Point type mismatch."), // code : 101115
    PAO_ID_MISMATCH(ApiErrorCategory.VALIDATION_FAILED, 116, "PAO Id mismatch", "PAO Id mismatch."), // code : 101116
    INVALID_DEPENDENT_FIELDS(ApiErrorCategory.VALIDATION_FAILED, 117, "Dependent fields are invalid", "Dependent fields are invalid."); // code : 101117
    
    
    private ApiErrorCategory category;
    private int code;
    private String title;
    private String defaultMessage;

    private static final Map<Integer, ApiErrorDetails> apiErrorDetails;

    static {
        ImmutableMap.Builder<Integer, ApiErrorDetails> validationFieldErrorsBuilder = ImmutableMap.builder();
        for (ApiErrorDetails errorDetails : ApiErrorDetails.values()) {
            if (errorDetails.getCategory() == ApiErrorCategory.VALIDATION_FAILED) {
                validationFieldErrorsBuilder.put(errorDetails.code, errorDetails);
            }
        }
        apiErrorDetails = validationFieldErrorsBuilder.build();
    }

    /**
     * Defines an error in the specified category. The error code will be category code + error ID.
     */
    ApiErrorDetails(ApiErrorCategory category, int errorId, String title, String defaultMessage) {
        this.code = category.getCode() + errorId;
        this.category = category;
        this.title = title;
        this.defaultMessage = defaultMessage;
    }

    /**
     * Defines an error under ApiErrorCategory.NONE.
     */
    ApiErrorDetails(int errorId, String title, String defaultMessage) {
        this(ApiErrorCategory.NONE, errorId, title, defaultMessage);
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

    /**
     * Return code in String format.
     */
    public String getCodeString() {
        return Integer.toString(code);
    }

}
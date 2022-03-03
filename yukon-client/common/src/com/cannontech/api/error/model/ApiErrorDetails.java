package com.cannontech.api.error.model;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

/*
 * Details of API errors.
 */
public enum ApiErrorDetails {
    // Exceptions
    OBJECT_ALREADY_EXISTS(101, "Object already exists", "An identical object already exists."), // code : 100101
    AUTHENTICATION_INVALID(102, "Authentication Invalid", "Username or password not valid."), // code : 100102
    NOT_AUTHORIZED(103, "Not authorized", "User not authorized to perform this transaction."), // code : 100103
    DATABASE_ERROR(104, "Database error", "Database error occured while performing this transaction."), // code : 100104
    METHOD_ARGUMENT_MISMATCH(105, "Method argument mismatch", "Method arguments are not matching."), // code : 100105
    BAD_REQUEST(106, "Bad request", "Bad request."), // code : 100106
    AUTHENTICATION_REQUIRED(107, "Authentication required", "Expired or invalid token"), // code : 100107
    NO_HANDLER_FOUND(108, "No handler found", "No handler found."), // code : 100108
    HTTP_REQUEST_METHOD_NOT_SUPPORTED(109, "HTTP request method not supported", "HTTP request method not supported."), // code : 100109
    DUPLICATE_VALUE(110, "Duplicate not Allowed", "Duplicate value not Allowed"), // code : 100110
    ONE_GEAR(111, "Only one gear Allowed", "Only one gear Allowed"), // code : 100111
    CONSTRAINT_VIOLATED(112, "Constraint Violated", "Constraint Violated"), // code : 100112

    // Validation Errors
    INVALID_VALUE(ApiErrorCategory.VALIDATION_FAILED, 101, "Invalid value", "Invalid value for the field."), // code : 101101
    FIELD_REQUIRED(ApiErrorCategory.VALIDATION_FAILED, 102, "Required field", "Field is required."), // code : 101102
    MAX_LENGTH_EXCEEDED(ApiErrorCategory.VALIDATION_FAILED, 103, "Max length exceeded", "Max length exceeded."), // code : 101103
    ILLEGAL_CHARACTERS(ApiErrorCategory.VALIDATION_FAILED, 104, "Illegal characters", "Invalid characters."), // code : 101104
    VALUE_OUTSIDE_VALID_RANGE(ApiErrorCategory.VALIDATION_FAILED, 105, "Invalid range", "Provided value is out of range."), // code : 101105
    ALREADY_EXISTS(ApiErrorCategory.VALIDATION_FAILED, 106, "Already exists", "Already exists."), // code : 101106
    DOES_NOT_EXISTS(ApiErrorCategory.VALIDATION_FAILED, 107, "Does not exist", "Does not exist."), // code : 101107
    NOT_SUPPORTED(ApiErrorCategory.VALIDATION_FAILED, 108, "Not supported", "Provided type not supported."), // code : 101108
    START_DATE_BEFORE_END_DATE(ApiErrorCategory.VALIDATION_FAILED, 109, "Start date must be before end date", "Start date must be before end date."), // code : 101109

    VALUE_BELOW_MINIMUM(ApiErrorCategory.VALIDATION_FAILED, 110, "Value below minimum", "Provided value is below the allowed minimum."), // code : 101110
    IS_NOT_POSITIVE(ApiErrorCategory.VALIDATION_FAILED, 111, "Must be a positive number", "Must be a positive number."), // code : 101111
    PAST_DATE(ApiErrorCategory.VALIDATION_FAILED, 112, "Date must be in the past", "Date must be in the past."), // code : 101112
    
    TYPE_MISMATCH(ApiErrorCategory.VALIDATION_FAILED, 113, "Type mismatch", "Type mismatch."), // code : 101113
    INVALID_FIELD_LENGTH(ApiErrorCategory.VALIDATION_FAILED, 114, "Field length must be valid", "Field length must be valid."), // code : 101114
    WHITELIST_CHARACTERS(ApiErrorCategory.VALIDATION_FAILED, 115, "Not a whitelisted characters", "Invalid whitelisted characters."), // code : 101115
    FUTURE_DATE(ApiErrorCategory.VALIDATION_FAILED, 116, "Date must be in the future", "Date must be in the future."); // code : 101116

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

    public int getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public ApiErrorCategory getCategory() {
        return category;
    }

    // This will return the type, url is fixed, with code appended
    public String getType() {
        return "/api/errors/" + getCode();
    }

    /**
     * Returns ApiErrorDetails for the specified code.
     */
    public static ApiErrorDetails getError(String code) {
        Integer codeValue = Optional.ofNullable(code).map(Integer::valueOf).orElse(null);
        return apiErrorDetails.get(codeValue);
    }

    /**
     * Return code in String format.
     */
    public String getCodeString() {
        return Integer.toString(code);
    }

}
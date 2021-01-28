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
    INVALID_STRING_LENGTH(ApiErrorCategory.VALIDATION_FAILED, 111, "Invalid string length", "Invalid string length."), // code : 101111
    IS_NOT_POSITIVE(ApiErrorCategory.VALIDATION_FAILED, 112, "Must be a positive number", "Must be a positive number."), // code : 101112
    POINT_OFFSET_NOT_AVAILABLE(ApiErrorCategory.VALIDATION_FAILED, 113, "Point offset not available", "Point offset not available."), // code : 101113
    POINT_TYPE_MISMATCH(ApiErrorCategory.VALIDATION_FAILED, 114, "Point type mismatch", "Point type mismatch."), // code : 101114
    PAO_ID_MISMATCH(ApiErrorCategory.VALIDATION_FAILED, 115, "Pao id mismatch", "Pao id mismatch."), // code : 101115
    PAO_TYPE_MISMATCH(ApiErrorCategory.VALIDATION_FAILED, 116, "Pao type mismatch", "Pao type mismatch."), // code : 101116
    PAST_DATE(ApiErrorCategory.VALIDATION_FAILED, 117, "Date must be in the past", "Date must be in the past."), // code : 101117
    INVALID_CONTROL_TYPE(ApiErrorCategory.VALIDATION_FAILED, 118, "Invalid control type", "Invalid control type."),// code : 101118
    INVALID_OPERATION(ApiErrorCategory.VALIDATION_FAILED, 119, "Invalid operation", "Invalid operation."),// code : 101119
    INVALID_POINT_ID(ApiErrorCategory.VALIDATION_FAILED, 120, "Invalid point id", "Invalid point Id."),// code : 101120
    INVALID_UPDATE_TYPE(ApiErrorCategory.VALIDATION_FAILED, 121, "Invalid update type", "Invalid update type."),// code : 101121
    INVALID_INITIAL_STATE(ApiErrorCategory.VALIDATION_FAILED, 122, "Invalid initial state", "Invalid initial state."),// code : 101122
    INVALID_ARCHIVE_TYPE(ApiErrorCategory.VALIDATION_FAILED, 123, "Archive type mismatch", " Archive type mismatch."), // code : 101123
    INVALID_ARCHIVE_TIME_INTERVAL(ApiErrorCategory.VALIDATION_FAILED, 124, "Invalid archive time interval", " Invalid archive time interval."), // code : 101124
    INVALID_REASONABILITY(ApiErrorCategory.VALIDATION_FAILED, 125, "Invalid lower limit", "Invalid lower limit."), // code : 101125
    INVALID_UPDATE_STYLE(ApiErrorCategory.VALIDATION_FAILED, 126, "Invalid update style", " Invalid update style."), // code : 101126
    INVALID_ARCHIVE_INTERVAL(ApiErrorCategory.VALIDATION_FAILED, 127, "Invalid archive interval", " Invalid archive interval."), // code : 101127
    INVALID_LIMIT_NUMBER(ApiErrorCategory.VALIDATION_FAILED, 128, "Invalid limit number", " Invalid limit number."), // code : 101128
    INVALID_TRANSLATION_PROPERTY_COUNT(ApiErrorCategory.VALIDATION_FAILED, 129, "Invalid translation property count", " Invalid translation property count."), // code : 101129
    INVALID_TRANSLATION_PROPERTY(ApiErrorCategory.VALIDATION_FAILED, 130, "Invalid translation property", " Invalid translation property."), // code : 101130
    INVALID_TRANSLATION_PROPERTY_VALUE(ApiErrorCategory.VALIDATION_FAILED, 131, "Invalid translation property value", " Invalid translation property value."), // code : 101131
    INVALID_SUPPORT_DIRECTION(ApiErrorCategory.VALIDATION_FAILED, 132, "Invalid support direction", " Invalid support direction."), // code : 101132
    INVALID_SIZE(ApiErrorCategory.VALIDATION_FAILED, 133, "Invalid Size", " Invalid Size."), // code : 101133

    COMBINATION_NOT_UNIQUE(ApiErrorCategory.VALIDATION_FAILED, 134, "Combination of the fields must be unique", "Combination must be unique."), // code : 101134
    NOT_UNIQUE(ApiErrorCategory.VALIDATION_FAILED, 135, "Value must be unique", "Must be unique."), // code : 101135
    INVALID_HEX_FORMAT(ApiErrorCategory.VALIDATION_FAILED, 136, "Key must be in hex format", "Key must be in hex format and 16 bytes long."), // code : 101136
    INVALID_HEX_LENGTH(ApiErrorCategory.VALIDATION_FAILED, 137, "Key must be 16 bytes long", "Key must be 16 bytes long."), // code : 101137
    INVALID_SOCKET_NUMBER(ApiErrorCategory.VALIDATION_FAILED, 138, "Invalid socket number", "Socket number must be 1025 when port type is not shared."); // code : 101138

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
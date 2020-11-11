package com.cannontech.api.exception;

import com.cannontech.api.error.model.ApiErrorDetails;

public class RestApiException extends RuntimeException {

    private ApiErrorDetails errorDetails;

    public RestApiException(ApiErrorDetails errorDetails) {
        super();
        this.errorDetails = errorDetails;
    }

    public RestApiException(ApiErrorDetails errorDetails, String defaultMessage) {
        super();
        this.errorDetails = errorDetails;
        this.errorDetails.setDefaultMessage(defaultMessage);
    }

    public ApiErrorDetails getErrorDetails() {
        return errorDetails;
    }
}

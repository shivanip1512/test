package com.cannontech.api.exception;

import com.cannontech.api.error.model.ApiErrorDetails;

public class MaxLengthExceededException extends RestApiException {

    public MaxLengthExceededException(ApiErrorDetails errorDetails) {
        super(errorDetails);
    }

    public MaxLengthExceededException(ApiErrorDetails errorDetails, String details) {
        super(errorDetails, details);
    }

}

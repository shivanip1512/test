package com.cannontech.api.exception;

import com.cannontech.api.error.model.ApiErrorDetails;

public class IllegalCharactersException extends RestApiException {

    public IllegalCharactersException(ApiErrorDetails errorDetails) {
        super(errorDetails);
    }

    public IllegalCharactersException(ApiErrorDetails errorDetails, String defaultMessage) {
        super(errorDetails, defaultMessage);
    }

}

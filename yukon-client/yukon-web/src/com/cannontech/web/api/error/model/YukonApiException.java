package com.cannontech.web.api.error.model;

import com.cannontech.api.error.model.ApiErrorDetails;

/**
 * Exception to be thrown by API code. This will be caught in the ApiExceptionHandler and result in an error response
 * with HttpStatus.INTERNAL_SERVER_ERROR.
 */
public class YukonApiException extends RuntimeException {
    private final ApiErrorDetails errorDetails;
    
    public YukonApiException(String message, ApiErrorDetails errorDetails) {
        super(message);
        this.errorDetails = errorDetails;
    }
    
    public YukonApiException(String message, Throwable cause, ApiErrorDetails errorDetails) {
        super(message, cause);
        this.errorDetails = errorDetails;
    }
    
    public ApiErrorDetails getApiErrorDetails() {
        return errorDetails;
    }
    
}

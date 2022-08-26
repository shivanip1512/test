package com.cannontech.web.api.error.model;

import com.cannontech.api.error.model.ApiErrorDetails;

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

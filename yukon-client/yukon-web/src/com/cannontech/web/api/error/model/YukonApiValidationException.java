package com.cannontech.web.api.error.model;

import com.cannontech.api.error.model.ApiErrorDetails;

/**
 * A field-specific validation exception to be thrown by API code. This will be caught in the ApiExceptionHandler and 
 * result in an error response with HttpStatus.UNPROCESSABLE_ENTITY.
 */
public class YukonApiValidationException extends YukonApiException {
    private final ApiErrorDetails validationErrorDetails;
    private final String detail;
    private final String field;
    private final String rejectedValue;
    private final Object[] parameters;
    
    public YukonApiValidationException(ApiErrorDetails validationErrorDetails,
            String detail, String field, String rejectedValue, Object[] parameters) {
        
        super(detail, null);
        this.validationErrorDetails = validationErrorDetails;
        this.detail = detail;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.parameters = parameters;
    }
    
    public YukonApiValidationException(Throwable cause, ApiErrorDetails validationErrorDetails,
            String detail, String field, String rejectedValue, Object[] parameters) {
        
        super(detail, cause, null);
        this.validationErrorDetails = validationErrorDetails;
        this.detail = detail;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.parameters = parameters;
    }

    public ApiErrorDetails getValidationErrorDetails() {
        return validationErrorDetails;
    }

    public String getDetail() {
        return detail;
    }

    public String getField() {
        return field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }

    public Object[] getParameters() {
        return parameters;
    }
    
}

package com.cannontech.web.common.dashboard.exception;

/**
 * Exception thrown when a widget parameter fails validation when creating a dashboard widget.
 */
public class WidgetParameterValidationException extends WidgetCreationException {
    private static final String keyBase = "yukon.web.modules.dashboard.exception.";
    private String parameterName;
    
    public WidgetParameterValidationException(String message, String parameterName, String errorKey, Object... args) {
        super(message, keyBase + errorKey, args);
        this.parameterName = parameterName;
    }
    
    public WidgetParameterValidationException(String message, Throwable cause, String parameterName, String errorKey, Object... args) {
        super(message, cause, keyBase + errorKey, args);
        this.parameterName = parameterName;
    }
    
    public String getParameterName() {
        return parameterName;
    }
}

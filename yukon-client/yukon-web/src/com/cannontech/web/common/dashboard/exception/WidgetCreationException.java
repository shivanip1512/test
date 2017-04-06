package com.cannontech.web.common.dashboard.exception;

import com.cannontech.common.exception.DisplayableException;

/**
 * Base class for all exceptions that can be thrown when attempting to create a dashboard widget.
 * These are DisplayableExceptions, which can be passed directly to i18n-aware jsp tags like i:inline and cti:msg2.
 */
public abstract class WidgetCreationException extends DisplayableException{
    
    public WidgetCreationException(String message) {
        super(message);
    }
    
    public WidgetCreationException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
    protected WidgetCreationException(String message, String key, Object... args) {
        super(message, key, args);
    }

    protected WidgetCreationException(String message, Throwable cause, String key, Object... args) {
        super(message, cause, key, args);
    }
}

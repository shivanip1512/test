package com.cannontech.common.exception;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * Class to make exception message i18nd.
 */
public abstract class DisplayableRuntimeException extends RuntimeException {
    private String key;
    private Object[] args;

    protected DisplayableRuntimeException(String message, String key, Object... args) {
        super(message);
        this.key = key;
        this.args = args;
    }

    protected DisplayableRuntimeException(String message, Throwable cause, String key,
            Object... args) {
        super(message, cause);
        this.key = key;
        this.args = args;
    }

    public MessageSourceResolvable getMessageSourceResolvable() {
        return new YukonMessageSourceResolvable(key, args);
    }
}

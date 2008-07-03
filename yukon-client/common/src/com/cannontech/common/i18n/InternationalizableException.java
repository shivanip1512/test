package com.cannontech.common.i18n;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

public class InternationalizableException extends RuntimeException {

    private final MessageSourceResolvable message;

    public InternationalizableException(String message) {
        super(message);
        this.message = new DefaultMessageSourceResolvable(null, null, message);
    }

    public InternationalizableException(String message, Throwable cause) {
        super(message, cause);
        this.message = new DefaultMessageSourceResolvable(null, null, message);
    }
    
    public InternationalizableException(MessageSourceResolvable message, Throwable cause) {
        super("See MSR for detail: " + message.toString(), cause);
        this.message = message;
        
    }

    public MessageSourceResolvable getMessageSourceResolvable() {
        return message;
    }
}

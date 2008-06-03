package com.cannontech.common.bulk.mapper;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Exception thrown when there is a problem when trying to map an object
 */
public class ObjectMappingException extends RuntimeException {

    private final MessageSourceResolvable message;

    public ObjectMappingException(String message) {
        super(message);
        this.message = new DefaultMessageSourceResolvable(null, null, message);
    }

    public ObjectMappingException(String message, Throwable cause) {
        super(message, cause);
        this.message = new DefaultMessageSourceResolvable(null, null, message);
    }
    
    public ObjectMappingException(MessageSourceResolvable message, Throwable cause) {
        super("See MSR for detail: " + message.toString(), cause);
        this.message = message;
        
    }

    public MessageSourceResolvable getMessageSourceResolvable() {
        return message;
    }
}

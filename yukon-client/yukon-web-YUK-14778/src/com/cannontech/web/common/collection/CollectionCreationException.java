package com.cannontech.web.common.collection;

public class CollectionCreationException extends RuntimeException {

    public CollectionCreationException(String message) {
        super(message);
    }

    public CollectionCreationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
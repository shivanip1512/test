package com.cannontech.common.util;

public class ApplicationIdUnknownException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String PREFIX = "Application ID unknown: ";
    
    public ApplicationIdUnknownException(String id) {
        super(PREFIX + id);
    }
}

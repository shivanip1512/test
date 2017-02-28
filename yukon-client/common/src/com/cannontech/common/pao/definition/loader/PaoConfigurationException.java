package com.cannontech.common.pao.definition.loader;

public class PaoConfigurationException extends RuntimeException {
    
    public PaoConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaoConfigurationException(String message) {
        super(message);
    }
}
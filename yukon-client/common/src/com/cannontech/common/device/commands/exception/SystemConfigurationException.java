package com.cannontech.common.device.commands.exception;


public class SystemConfigurationException extends CommandCompletionException{

    public SystemConfigurationException(String message) {
        super(message);
    }
    
    public SystemConfigurationException(String message, Throwable cause) {
        super(message,cause);
    }
}

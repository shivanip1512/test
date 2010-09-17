package com.cannontech.core.dao;

public class StatusPointMonitorMessageProcessorNotFoundException extends NotFoundException {

    public StatusPointMonitorMessageProcessorNotFoundException() {
        super("Status Point Monitor Does Not Exist.");
    }
    
    public StatusPointMonitorMessageProcessorNotFoundException(String message) {
        super(message);
    }
    
    public StatusPointMonitorMessageProcessorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
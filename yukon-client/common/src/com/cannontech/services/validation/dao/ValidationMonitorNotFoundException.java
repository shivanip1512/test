package com.cannontech.services.validation.dao;

import com.cannontech.core.dao.NotFoundException;

public class ValidationMonitorNotFoundException extends NotFoundException {

    public ValidationMonitorNotFoundException() {
        super("Validation Monitor Does Not Exist.");
    }
    
    public ValidationMonitorNotFoundException(String message) {
        super(message);
    }
    
    public ValidationMonitorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
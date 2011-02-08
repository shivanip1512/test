package com.cannontech.core.dao;

public class EnergyCompanyNameUnavailableException extends DuplicateException {
    
    public EnergyCompanyNameUnavailableException(String message) {
        super(message);
    }

    public EnergyCompanyNameUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
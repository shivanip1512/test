package com.cannontech.core.dao;

public class EnergyCompanyNotFoundException extends NotFoundException {
    
    public EnergyCompanyNotFoundException(String message) {
        super(message);
    }
    
    public EnergyCompanyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}

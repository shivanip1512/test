package com.cannontech.dr.assetavailability.service.impl;

public class NoInventoryException extends Exception {
    public NoInventoryException(String message, Exception cause) {
        super(message, cause);
    }
    
    public NoInventoryException(String message) {
        super(message);
    }
}

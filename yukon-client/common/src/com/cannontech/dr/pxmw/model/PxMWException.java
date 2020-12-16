package com.cannontech.dr.pxmw.model;

public class PxMWException extends RuntimeException {
    //HTTP response status codes
    //Received from device
    private int statusCode;
    public PxMWException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public PxMWException(String message) {
        super(message);
    }
    
    public PxMWException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public int getStatusCode() {
       return statusCode; 
    }
}
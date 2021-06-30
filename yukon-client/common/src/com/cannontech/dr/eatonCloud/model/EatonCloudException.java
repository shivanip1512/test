package com.cannontech.dr.eatonCloud.model;

public class EatonCloudException extends RuntimeException {
    //HTTP response status codes
    //Received from device
    private int statusCode;
    public EatonCloudException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public EatonCloudException(String message) {
        super(message);
    }
    
    public EatonCloudException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public int getStatusCode() {
       return statusCode; 
    }
}
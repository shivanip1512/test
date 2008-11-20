package com.cannontech.stars.util;

/**
 * This Stars Runtime Exception is used to catch any client
 * request errors and return exceptions with errorCode.
 * 
 * In case of web service clients, errorCode along with error message may be sent 
 * as failures in the response, for possible programmatic handling; 
 * other exceptions may result in soap-faults.
 * 
 * @author mmalekar
 */
public class StarsClientRequestException extends RuntimeException {

    public static final String INVALID_ARGUMENT = "InvalidArgument";
    public static final String ACCOUNT_NOT_FOUND = "AccountNotFound";
    public static final String DEVICE_NOT_FOUND = "DeviceNotFound";
    public static final String DEVICE_ALREADY_EXISTS = "DeviceAlreadyExists";
    public static final String DEVICE_ASSIGNED_TO_ANOTHER_ACCOUNT = "DeviceAssignedToAnotherAccount";
    public static final String SERIAL_NUMBER_ALREADY_EXISTS = "SerialNumberAlreadyExists";    

    private String errorCode = "";

    public String getErrorCode() {
        return errorCode;
    }

    public StarsClientRequestException() {
        super();
    }

    public StarsClientRequestException(String message) {
        super(message);
    }

    public StarsClientRequestException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public StarsClientRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public StarsClientRequestException(String errorCode, String message,
            Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}

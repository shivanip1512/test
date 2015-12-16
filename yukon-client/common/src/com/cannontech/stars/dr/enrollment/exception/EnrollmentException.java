package com.cannontech.stars.dr.enrollment.exception;

public class EnrollmentException extends RuntimeException {
    
    private String key = "yukon.web.modules.operator.enrollment.error.failed";
    
    public EnrollmentException() {
    }
    
    public EnrollmentException (String message) {
        super(message);
    }

    public EnrollmentException (String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getKey() {
        return key;
    }
}
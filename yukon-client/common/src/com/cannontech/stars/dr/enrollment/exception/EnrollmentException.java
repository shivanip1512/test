package com.cannontech.stars.dr.enrollment.exception;

public class EnrollmentException extends RuntimeException {
    
    private String key = "yukon.web.modules.operator.enrollment.error.failed";
    
    public EnrollmentException (String message) {
        super(message);
    }

    public EnrollmentException() {
    }
    
    public String getKey() {
        return key;
    }
}
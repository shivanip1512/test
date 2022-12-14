package com.cannontech.stars.dr.enrollment.exception;

public class EnrollmentSystemConfigurationException extends EnrollmentException {

    private String key = "yukon.web.modules.operator.enrollment.error.systemConfiguration";
    
    public EnrollmentSystemConfigurationException() {
        super();
    }
    
    public EnrollmentSystemConfigurationException(String message) {
        super(message);
    }
    
    public EnrollmentSystemConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    @Override
    public String getKey() {
        return key;
    }
}

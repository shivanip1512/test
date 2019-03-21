package com.cannontech.stars.dr.enrollment.exception;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public class EnrollmentException extends RuntimeException {
    
    private String key = "yukon.web.modules.operator.enrollment.error.failed";
    private YukonMessageSourceResolvable detailMessage;
    
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

    public void setDetailMessage(YukonMessageSourceResolvable detailMessage) {
        this.detailMessage = detailMessage;
    }
    
    public YukonMessageSourceResolvable getDetailMessage() {
        return detailMessage;
    }
}
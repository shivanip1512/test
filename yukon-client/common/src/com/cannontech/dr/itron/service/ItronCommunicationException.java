package com.cannontech.dr.itron.service;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public class ItronCommunicationException extends RuntimeException {
    
    public ItronCommunicationException(String message) {
        super(message);
    }

    public ItronCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public YukonMessageSourceResolvable getItronMessage() {
        return new YukonMessageSourceResolvable("yukon.web.modules.operator.hardware.error.itronCommunicationError");
    }
}
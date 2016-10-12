package com.cannontech.web.tools.mapping.model;

import com.cannontech.common.exception.DisplayableException;

public class NmNetworkException extends DisplayableException {
    private static final String keyBase = "yukon.web.modules.operator.mapNetwork.exception.";
    
    public NmNetworkException(String message, String key) {
        super(message, keyBase + key, new Object[0]);
    }
    
    public NmNetworkException(String message, Throwable cause, String key) {
        super(message, cause, keyBase + key, new Object[0]);
    }
    
    public NmNetworkException(String message, String key, Object... args) {
        super(message, keyBase + key, args);
    } 
}
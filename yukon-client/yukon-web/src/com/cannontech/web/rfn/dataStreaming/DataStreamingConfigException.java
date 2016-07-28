package com.cannontech.web.rfn.dataStreaming;

import com.cannontech.common.exception.DisplayableException;

public class DataStreamingConfigException extends DisplayableException {
    private static final String keyBase = "yukon.web.modules.tools.bulk.dataStreaming.verification.exception.";
    
    public DataStreamingConfigException(String message, String key) {
        super(message, keyBase + key, new Object[0]);
    }
    
    public DataStreamingConfigException(String message, Throwable cause, String key) {
        super(message, cause, keyBase + key, new Object[0]);
    }
    
    public DataStreamingConfigException(String message, String key, Object... args) {
        super(message, keyBase + key, args);
    }
    
    
}

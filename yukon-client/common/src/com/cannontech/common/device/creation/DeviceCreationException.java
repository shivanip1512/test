package com.cannontech.common.device.creation;

import com.cannontech.common.exception.DisplayableRuntimeException;
import com.cannontech.common.pao.PaoType;

public class DeviceCreationException extends DisplayableRuntimeException {
    
    public static enum Type {
        UNKNOWN,
        GUID_ALREADY_EXISTS,
        GUID_DOES_NOT_EXIST,
    }
    
    private Type type = Type.UNKNOWN;

    private static final String keyBase = "yukon.exception.deviceCreationException.";

    // This constructor is left for those DeviceCreationException where i18 is not required. 
    public DeviceCreationException(String message) {
        super(message);
    }

    public DeviceCreationException(String message, String key) {
        super(message, keyBase + key);
    }
    
    public DeviceCreationException(String message, String key, Type type, Throwable cause) {
        super(message, keyBase + key, cause);
        this.type = type;
    }
    
    public DeviceCreationException(String message, String key, Type type) {
        super(message, keyBase + key);
        this.type = type;
    }

    public DeviceCreationException(String message, String key, Throwable cause) {
        super(message, cause, keyBase + key);
    }

    public DeviceCreationException(String message, String key, String name, Throwable cause) {
        super(message, cause, keyBase + key, name);
    }

    public DeviceCreationException(String message, String key, String newDeviceName, String templateName) {
        super(message, keyBase + key, newDeviceName, templateName);
    }

    public DeviceCreationException(String message, String key, int address) {
        super(message, keyBase + key, address);
    }

    public DeviceCreationException(String message, String key, String name) {
        super(message, keyBase + key, name);
    }

    public DeviceCreationException(String message, String key, String deviceName, String templateName, Throwable cause) {
        super(message, cause, keyBase + key,  deviceName, templateName);
    }

    public DeviceCreationException(String message, String key, String ecName, PaoType paoType) {
        super(message, keyBase + key, ecName, paoType);
    }
    
    public Type getType() {
        return type;
    }
}

package com.cannontech.common.device.groups;

import java.util.List;

public class DeviceGroupInUseException extends RuntimeException {
    static String msgKey = "yukon.exception.deviceGroupInUseException";
    private List<DeviceGroupInUse> references;
    
    public DeviceGroupInUseException(String message, List<DeviceGroupInUse> references) {
        super (message);
        this.references = references;
    }

    public DeviceGroupInUseException(String message, Throwable cause, List<DeviceGroupInUse> references) {
        super (message, cause);
        this.references = references;
    }

    public List<DeviceGroupInUse> getReferences() {
        return references;
    }
    
    public String getMessageKey() {
        return msgKey;
    }
}

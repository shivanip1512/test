package com.cannontech.common.device;

public enum DeviceEventEnum {

    MOVE_IN("Move In Reading"),
    MOVE_OUT("Move Out Reading");
    
    private String eventType;

    DeviceEventEnum(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return this.eventType;
    }
    
    
}

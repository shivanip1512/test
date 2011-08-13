package com.cannontech.amr.deviceread.dao;

import org.springframework.context.MessageSourceResolvable;

public class DeviceAttributeReadError {
    private DeviceAttributeReadErrorType type;
    private MessageSourceResolvable summary;
    private MessageSourceResolvable detail;
    
    public DeviceAttributeReadError(DeviceAttributeReadErrorType type,
            MessageSourceResolvable summary, MessageSourceResolvable detail) {
        this.type = type;
        this.summary = summary;
        this.detail = detail;
    }

    public DeviceAttributeReadError(DeviceAttributeReadErrorType type,
            MessageSourceResolvable summary) {
        this.type = type;
        this.summary = summary;
    }
    
    public DeviceAttributeReadErrorType getType() {
        return type;
    }
    
    public MessageSourceResolvable getSummary() {
        return summary;
    }
    
    public MessageSourceResolvable getDetail() {
        return detail;
    }
    
    @Override
    public String toString() {
        return type.name() + ": " + summary + " (" + detail + ")";
    }
}

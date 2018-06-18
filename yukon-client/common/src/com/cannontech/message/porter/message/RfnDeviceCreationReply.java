package com.cannontech.message.porter.message;

import com.cannontech.message.DeviceCreationDescriptor;

public class RfnDeviceCreationReply {

    private DeviceCreationDescriptor descriptor;
    private boolean success;
    
    public RfnDeviceCreationReply(DeviceCreationDescriptor descriptor, boolean success) {
        this.descriptor = descriptor;
        this.success = success;
    }
    
    public RfnDeviceCreationReply() {
    }

    public DeviceCreationDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(DeviceCreationDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

package com.cannontech.common.device;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

public class YukonDevice {
    private int deviceId;
    private int type;

    public YukonDevice(int deviceId, int type) {
        super();
        this.deviceId = deviceId;
        this.type = type;
    }

    public YukonDevice() {
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("deviceId", getDeviceId());
        tsc.append("type", getType());
        return tsc.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof YukonDevice == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        YukonDevice device = (YukonDevice) obj;
        return new EqualsBuilder().append(deviceId, device.getDeviceId())
                                  .append(type, device.getType())
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(deviceId).append(type).toHashCode();
    }

}

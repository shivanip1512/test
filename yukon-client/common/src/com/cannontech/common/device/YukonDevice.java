package com.cannontech.common.device;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.YukonPao;

public class YukonDevice extends YukonPao {
    public YukonDevice(int deviceId, int type) {
        super(deviceId, type);
    }

    public YukonDevice() {
    }

    public int getDeviceId() {
        return getPaoId();
    }

    public void setDeviceId(int deviceId) {
        setPaoId(deviceId);
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
        return new EqualsBuilder().append(getDeviceId(), device.getDeviceId())
                                  .append(getType(), device.getType())
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getDeviceId()).append(getType()).toHashCode();
    }

}

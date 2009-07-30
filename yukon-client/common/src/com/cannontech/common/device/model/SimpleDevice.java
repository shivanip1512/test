package com.cannontech.common.device.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;

public class SimpleDevice extends YukonPao {
    public SimpleDevice(int deviceId, int type) {
        this(deviceId, PaoType.getForId(type));
    }

    public SimpleDevice(int deviceId, PaoType type) {
        super(deviceId, type.getDeviceTypeId());
    }
    
    public SimpleDevice() {
    }

    public int getDeviceId() {
        return getPaoId();
    }

    public void setDeviceId(int deviceId) {
        setPaoId(deviceId);
    }
    
    public PaoType getDeviceType() {
        return PaoType.getForId(getType());
    }
    
    public void setDeviceType(PaoType deviceType) {
        setType(deviceType.getDeviceTypeId());
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
        if (obj instanceof SimpleDevice == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        SimpleDevice device = (SimpleDevice) obj;
        return new EqualsBuilder().append(getDeviceId(), device.getDeviceId())
                                  .append(getType(), device.getType())
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getDeviceId()).append(getType()).toHashCode();
    }

}

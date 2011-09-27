package com.cannontech.common.device.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;

public final class SimpleDevice implements YukonDevice {
    private int deviceId;
	private PaoType type;

	public SimpleDevice(int deviceId, int type) {
        this(deviceId, PaoType.getForId(type));
    }

    public SimpleDevice(int deviceId, PaoType type) {
		this.deviceId = deviceId;
		this.type = type;
    }
    
    public SimpleDevice(YukonPao pao) {
    	PaoUtils.validateDeviceType(pao);
    	this.deviceId = pao.getPaoIdentifier().getPaoId();
    	this.type = pao.getPaoIdentifier().getPaoType();
    }

    public SimpleDevice() {
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
    }
    
    public PaoType getDeviceType() {
        return type;
    }
    
    public void setDeviceType(PaoType deviceType) {
		type = deviceType;
    }
    
    public int getType() {
    	return type.getDeviceTypeId();
    }
    
    public void setType(int type) {
    	this.type = PaoType.getForId(type);
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
    	return new PaoIdentifier(deviceId, type);
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("deviceId", getDeviceId());
        tsc.append("type", type);
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
                                  .append(getDeviceType(), device.getDeviceType())
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getDeviceId()).append(getDeviceType()).toHashCode();
    }

}

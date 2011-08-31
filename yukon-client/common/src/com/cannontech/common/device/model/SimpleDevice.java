package com.cannontech.common.device.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;

public final class SimpleDevice implements YukonDevice, YukonPao {
    private PaoIdentifier paoIdentifier;

	public SimpleDevice(int deviceId, int type) {
        this(deviceId, PaoType.getForId(type));
    }
	
	public SimpleDevice(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}

    public SimpleDevice(int deviceId, PaoType paoType) {
		this(new PaoIdentifier(deviceId, paoType));
    }
    
    public SimpleDevice(YukonPao pao) {
    	PaoUtils.validateDeviceType(pao);
    	this.paoIdentifier = pao.getPaoIdentifier();
    }

    public int getDeviceId() {
        return paoIdentifier.getPaoId();
    }
    
    public PaoType getDeviceType() {
        return paoIdentifier.getPaoType();
    }
    
    public int getType() {
    	return paoIdentifier.getPaoType().getDeviceTypeId();
    }
    
    public void setType(int type) {
    	PaoType paoType = PaoType.getForId(type);
    	paoIdentifier = new PaoIdentifier(paoIdentifier.getPaoId(), paoType);
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
    	return paoIdentifier;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("deviceId", getDeviceId());
        tsc.append("type", paoIdentifier.getPaoType());
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

package com.cannontech.common.device.definition.model;

import org.springframework.core.style.ToStringCreator;

public class DevicePointIdentifier {
    private int offset;
    private int type;
    
    public DevicePointIdentifier(int type, int offset) {
        this.offset = offset;
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }
    
    public int getType() {
        return type;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + offset;
        result = PRIME * result + type;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        final DevicePointIdentifier other = (DevicePointIdentifier) obj;
        if (offset != other.offset)
            return false;
        if (type != other.type)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("type", getType());
        tsc.append("offset", getOffset());
        return tsc.toString();
    }
    
    public boolean isComparableTo(DevicePointIdentifier identifier) {
    	if( offset != identifier.getOffset())
    		return false;
    	if( type != identifier.getType())
    		return false;
    	return true;
    }
}

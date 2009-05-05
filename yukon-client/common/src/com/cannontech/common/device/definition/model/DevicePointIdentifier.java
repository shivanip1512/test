package com.cannontech.common.device.definition.model;

import org.springframework.core.style.ToStringCreator;

public class DevicePointIdentifier implements Comparable<DevicePointIdentifier> {
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
    
    public int compareTo(DevicePointIdentifier o) {

        if (o == null) {
            return 0;
        }
    	if( o.getType() == getType()) {	//compare type
   			return ( getOffset() < o.getOffset()? -1 : (getOffset()==o.getOffset()? 0 : 1));//compare offset
    	}
		return ( getType() < o.getType()? -1 : (getType()==o.getType()? 0 : 1));
    }
    
    /**
     * @param identifier
     * @return
     * @deprecated This does the same thing as equals, but in an unsafe way
     */
    @Deprecated
    public boolean isComparableTo(DevicePointIdentifier identifier) {
    	if( offset != identifier.getOffset())
    		return false;
    	if( type != identifier.getType())
    		return false;
    	return true;
    }
}
package com.cannontech.common.device.definition.model;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.core.style.ToStringCreator;

public class PointIdentifier implements Comparable<PointIdentifier> {
    private int offset;
    private int type;
    
    public PointIdentifier(int type, int offset) {
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
        final PointIdentifier other = (PointIdentifier) obj;
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
    
    @Override
    public int compareTo(PointIdentifier o) {
        return new CompareToBuilder()
            .append(getType(), o.getType())
            .append(getOffset(), o.getOffset())
            .toComparison();
    }
    
    /**
     * @param identifier
     * @return
     * @deprecated This does the same thing as equals, but in an unsafe way
     */
    @Deprecated
    public boolean isComparableTo(PointIdentifier identifier) {
    	if( offset != identifier.getOffset())
    		return false;
    	if( type != identifier.getType())
    		return false;
    	return true;
    }
}
package com.cannontech.common.device.definition.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;

import com.cannontech.database.data.point.PointType;

public class PointIdentifier implements Comparable<PointIdentifier>, Serializable {
    private int offset;
    private PointType type;
    
    /**
     * @param type
     * @param offset
     * @deprecated Use the PointType Enum version.
     */
    @Deprecated
    public PointIdentifier(int type, int offset) {
        this.offset = offset;
        this.type = PointType.getForId(type);
    }

    /**
     * @param type
     * @param offset
     * @deprecated Use the PointType Enum version.
     */
    @Deprecated
    public PointIdentifier(String type, int offset) {
        this.offset = offset;
        this.type = PointType.getForString(type);
    }
    
    public PointIdentifier(PointType type, int offset) {
    	this.offset = offset;
    	this.type = type;
    }
    
    public int getOffset() {
        return offset;
    }
    
    /**
     * @return
     * @deprecated Use the getPointType version that uses an Enum.
     */
    @Deprecated
    public int getType() {
    	return type.getPointTypeId();
    }
    
    public PointType getPointType() {
        return type;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + offset;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointIdentifier other = (PointIdentifier) obj;
		if (offset != other.offset)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return type + ":" + offset;
    }
    
    @Override
    public int compareTo(PointIdentifier o) {
        return new CompareToBuilder()
            .append(getPointType(), o.getPointType())
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
    	if( type != identifier.getPointType())
    		return false;
    	return true;
    }
}
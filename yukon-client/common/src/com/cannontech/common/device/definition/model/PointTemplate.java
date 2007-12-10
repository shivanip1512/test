package com.cannontech.common.device.definition.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * Interface which represents a template which can be used to create point
 * instances
 */
public class PointTemplate implements Comparable<PointTemplate> {
    private DevicePointIdentifier devicePointIdentifier;
    private String name = null;
    private double multiplier = 1.0;
    private int unitOfMeasure = PointUnits.UOMID_INVALID;
    private int stateGroupId = StateGroupUtils.SYSTEM_STATEGROUPID;
    private boolean shouldInitialize = false;

    public PointTemplate(int type, int offset) {
        devicePointIdentifier = new DevicePointIdentifier(type, offset);
    }

    public int getOffset() {
        return devicePointIdentifier.getOffset();
    }

    public int getType() {
        return devicePointIdentifier.getType();
    }

    public DevicePointIdentifier getDevicePointIdentifier() {
        return devicePointIdentifier;
    }

    public PointTemplate(String name, int type, int offset, double multiplier,
            int unitOfMeasure, int stateGroupId, boolean shouldInitialize) {
        devicePointIdentifier = new DevicePointIdentifier(type, offset);
        this.name = name;
        this.multiplier = multiplier;
        this.unitOfMeasure = unitOfMeasure;
        this.stateGroupId = stateGroupId;
        this.shouldInitialize = shouldInitialize;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShouldInitialize() {
        return shouldInitialize;
    }

    public void setShouldInitialize(boolean shouldInitialize) {
        this.shouldInitialize = shouldInitialize;
    }

    public int getStateGroupId() {
        return stateGroupId;
    }

    public void setStateGroupId(int stateGroupId) {
        this.stateGroupId = stateGroupId;
    }

    public int getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(int unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public int compareTo(PointTemplate o) {

        if (o == null) {
            return 0;
        }
        if( getName().compareTo(o.getName()) == 0) {
        	if( o.getType() == getType()) {	//compare type
       			return ( getOffset() < o.getOffset()? -1 : (getOffset()==o.getOffset()? 0 : 1));//compare offset
        	}
    		return ( getType() < o.getType()? -1 : (getType()==o.getType()? 0 : 1));
        }
        	
        return getName().compareTo(o.getName());

    }

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
            prime * result + ((devicePointIdentifier == null) ? 0 : devicePointIdentifier
                .hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        final PointTemplate other = (PointTemplate) obj;
        if (devicePointIdentifier == null) {
            if (other.devicePointIdentifier != null)
                return false;
        } else if (!devicePointIdentifier.equals(other.devicePointIdentifier))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
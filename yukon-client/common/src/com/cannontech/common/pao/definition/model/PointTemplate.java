package com.cannontech.common.pao.definition.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * Interface which represents a template which can be used to create point
 * instances
 */
public class PointTemplate implements Comparable<PointTemplate> {
    private PointIdentifier pointIdentifier;
    private String name = null;
    private double multiplier = 1.0;
    private int unitOfMeasure = PointUnits.UOMID_INVALID;
    private int stateGroupId = StateGroupUtils.STATEGROUP_ANALOG;
    private int decimalPlaces = PointUnit.DEFAULT_DECIMAL_PLACES;

    public PointTemplate(PointType type, int offset) {
        pointIdentifier = new PointIdentifier(type, offset);
    }
    
    /**
     * @deprecated Use PointType version.
     * @param type
     * @param offset
     */
    @Deprecated
    public PointTemplate(int type, int offset) {
        pointIdentifier = new PointIdentifier(type, offset);
    }

    public int getOffset() {
        return pointIdentifier.getOffset();
    }

    /**
     * @deprecated Use getPointType version.
     */
    @Deprecated
    public int getType() {
        return pointIdentifier.getType();
    }
    
    public PointType getPointType() {
        return pointIdentifier.getPointType();
    }

    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }

    /**
     * @deprecated Use the PointType version.
     * @param name
     * @param type
     * @param offset
     * @param multiplier
     * @param unitOfMeasure
     * @param stateGroupId
     * @param decimalPlaces
     */
    @Deprecated
    public PointTemplate(String name, int type, int offset, double multiplier,
            int unitOfMeasure, int stateGroupId, int decimalPlaces) {
        pointIdentifier = new PointIdentifier(type, offset);
        this.name = name;
        this.multiplier = multiplier;
        this.unitOfMeasure = unitOfMeasure;
        this.stateGroupId = stateGroupId;
        this.decimalPlaces = decimalPlaces;
    }
    
    public PointTemplate(String name, PointType type, int offset, double multiplier,
            int unitOfMeasure, int stateGroupId, int decimalPlaces) {
        pointIdentifier = new PointIdentifier(type, offset);
        this.name = name;
        this.multiplier = multiplier;
        this.unitOfMeasure = unitOfMeasure;
        this.stateGroupId = stateGroupId;
        this.decimalPlaces = decimalPlaces;
    }
    
    public PointTemplate(String name, PointIdentifier pointIdentifier, double multiplier,
            int unitOfMeasure, int stateGroupId, int decimalPlaces) {
        this.pointIdentifier = pointIdentifier;
        this.name = name;
        this.multiplier = multiplier;
        this.unitOfMeasure = unitOfMeasure;
        this.stateGroupId = stateGroupId;
        this.decimalPlaces = decimalPlaces;
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
    
    public int getDecimalPlaces() {
        return decimalPlaces;
    }
    
    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
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
            prime * result + ((pointIdentifier == null) ? 0 : pointIdentifier
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
        if (pointIdentifier == null) {
            if (other.pointIdentifier != null)
                return false;
        } else if (!pointIdentifier.equals(other.pointIdentifier))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (multiplier != other.multiplier)
        	return false;
        if (unitOfMeasure != other.unitOfMeasure)
        	return false;
        if (stateGroupId != other.stateGroupId)
        	return false;
        return true;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("type", getPointType());
        tsc.append("offset", getOffset());
        tsc.append("name", getName());
        return tsc.toString();
    }

}
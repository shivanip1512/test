package com.cannontech.common.pao.definition.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.data.point.StateControlType;
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
    private PointArchiveInterval pointArchiveInterval = PointArchiveInterval.ZERO;
    private PointArchiveType pointArchiveType = PointArchiveType.NONE;

    // Only valid for Status points
    private int initialState = StateGroupUtils.DEFAULT_STATE;
    private int controlOffset = 1;
    private ControlType controlType = ControlType.NONE;
    private StateControlType stateZeroControl = StateControlType.OPEN;
    private StateControlType stateOneControl = StateControlType.CLOSE;

    // Only valid for Calculated points
    private CalcPointInfo calcPointInfo = null;
    
    public PointTemplate(PointType type, int offset) {
        pointIdentifier = new PointIdentifier(type, offset);
    }
    
    /**
     * @deprecated Use PointType version.
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

    public void setInitialState(int initialState) {
        this.initialState = initialState;
    }
    
    public int getInitialState() {
        return initialState;
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

    public int getControlOffset() {
        return controlOffset;
    }

    public void setControlOffset(int controlOffset) {
        this.controlOffset = controlOffset;
    }

    public ControlType getControlType() {
        return controlType;
    }

    public void setControlType(ControlType controlType) {
        this.controlType = controlType;
    }

    public StateControlType getStateZeroControl() {
        return stateZeroControl;
    }

    public void setStateZeroControl(StateControlType stateZeroControl) {
        this.stateZeroControl = stateZeroControl;
    }

    public StateControlType getStateOneControl() {
        return stateOneControl;
    }

    public void setStateOneControl(StateControlType stateOneControl) {
        this.stateOneControl = stateOneControl;
    }

    public PointArchiveType getPointArchiveType() {
		return pointArchiveType;
	}
    
    public void setPointArchiveType(PointArchiveType pointArchiveType) {
		this.pointArchiveType = pointArchiveType;
	}
    
    public PointArchiveInterval getPointArchiveInterval() {
		return pointArchiveInterval;
	}
    
    public void setPointArchiveInterval(PointArchiveInterval pointArchiveInterval) {
		this.pointArchiveInterval = pointArchiveInterval;
	}
    
    public CalcPointInfo getCalcPointInfo() {
        return calcPointInfo;
    }

    public void setCalcPointInfo(CalcPointInfo calcPointInfo) {
        this.calcPointInfo = calcPointInfo;
    }

    public int compareTo(PointTemplate o) {
        
        if (o == null) {
            return 0;
        } else {
            return getName().compareToIgnoreCase(o.getName());
        }
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
        PointTemplate other = (PointTemplate) obj;
        if (calcPointInfo == null) {
            if (other.calcPointInfo != null)
                return false;
        } else if (!calcPointInfo.equals(other.calcPointInfo))
            return false;
        if (controlOffset != other.controlOffset)
            return false;
        if (controlType != other.controlType)
            return false;
        if (decimalPlaces != other.decimalPlaces)
            return false;
        if (initialState != other.initialState)
            return false;
        if (Double.doubleToLongBits(multiplier) != Double.doubleToLongBits(other.multiplier))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (pointArchiveInterval != other.pointArchiveInterval)
            return false;
        if (pointArchiveType != other.pointArchiveType)
            return false;
        if (pointIdentifier == null) {
            if (other.pointIdentifier != null)
                return false;
        } else if (!pointIdentifier.equals(other.pointIdentifier))
            return false;
        if (stateGroupId != other.stateGroupId)
            return false;
        if (stateOneControl != other.stateOneControl)
            return false;
        if (stateZeroControl != other.stateZeroControl)
            return false;
        if (unitOfMeasure != other.unitOfMeasure)
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
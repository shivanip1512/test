package com.cannontech.common.pao.definition.model;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * Interface which represents a template which can be used to create point
 * instances
 */
public class PointTemplate implements Comparable<PointTemplate> {
  
    public static double DEFAULT_DATA_OFFSET = 0.0;
    
    private final PointIdentifier pointIdentifier;
    private String name = null;
    private double multiplier = 1.0;
    private double dataOffset = DEFAULT_DATA_OFFSET;
    private int unitOfMeasure = UnitOfMeasure.INVALID.getId();
    private int stateGroupId = StateGroupUtils.STATEGROUP_ANALOG;
    private int decimalPlaces = PointUnit.DEFAULT_DECIMAL_PLACES;
    private PointArchiveInterval pointArchiveInterval = PointArchiveInterval.ZERO;
    private PointArchiveType pointArchiveType = PointArchiveType.NONE;

    // Only valid for Status points
    private int initialState = StateGroupUtils.DEFAULT_STATE;
    private int controlOffset = 1;
    private StatusControlType controlType = StatusControlType.NONE;
    private ControlStateType stateZeroControl = ControlStateType.OPEN;
    private ControlStateType stateOneControl = ControlStateType.CLOSE;

    // Only valid for Calculated points
    private CalcPointInfo calcPointInfo = null;

    public PointTemplate(PointIdentifier pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }

    public PointTemplate(String name, PointType type, int offset, double multiplier,
            int unitOfMeasure, int stateGroupId, int decimalPlaces) {
        this.pointIdentifier = new PointIdentifier(type, offset);
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

    public int getOffset() {
        return pointIdentifier.getOffset();
    }

    public PointType getPointType() {
        return pointIdentifier.getPointType();
    }

    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
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

    public StatusControlType getControlType() {
        return controlType;
    }

    public void setControlType(StatusControlType controlType) {
        this.controlType = controlType;
    }

    public ControlStateType getStateZeroControl() {
        return stateZeroControl;
    }

    public void setStateZeroControl(ControlStateType stateZeroControl) {
        this.stateZeroControl = stateZeroControl;
    }

    public ControlStateType getStateOneControl() {
        return stateOneControl;
    }

    public void setStateOneControl(ControlStateType stateOneControl) {
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
    
    public double getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(double dataOffset) {
        this.dataOffset = dataOffset;
    }

    @Override
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
        result = prime * result + ((calcPointInfo == null) ? 0 : calcPointInfo.hashCode());
        result = prime * result + controlOffset;
        result = prime * result + ((controlType == null) ? 0 : controlType.hashCode());
        long temp;
        temp = Double.doubleToLongBits(dataOffset);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + decimalPlaces;
        result = prime * result + initialState;
        temp = Double.doubleToLongBits(multiplier);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((pointArchiveInterval == null) ? 0 : pointArchiveInterval.hashCode());
        result = prime * result + ((pointArchiveType == null) ? 0 : pointArchiveType.hashCode());
        result = prime * result + ((pointIdentifier == null) ? 0 : pointIdentifier.hashCode());
        result = prime * result + stateGroupId;
        result = prime * result + ((stateOneControl == null) ? 0 : stateOneControl.hashCode());
        result = prime * result + ((stateZeroControl == null) ? 0 : stateZeroControl.hashCode());
        result = prime * result + unitOfMeasure;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PointTemplate other = (PointTemplate) obj;
        if (calcPointInfo == null) {
            if (other.calcPointInfo != null) {
                return false;
            }
        } else if (!calcPointInfo.equals(other.calcPointInfo)) {
            return false;
        }
        if (controlOffset != other.controlOffset) {
            return false;
        }
        if (controlType != other.controlType) {
            return false;
        }
        if (Double.doubleToLongBits(dataOffset) != Double.doubleToLongBits(other.dataOffset)) {
            return false;
        }
        if (decimalPlaces != other.decimalPlaces) {
            return false;
        }
        if (initialState != other.initialState) {
            return false;
        }
        if (Double.doubleToLongBits(multiplier) != Double.doubleToLongBits(other.multiplier)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (pointArchiveInterval != other.pointArchiveInterval) {
            return false;
        }
        if (pointArchiveType != other.pointArchiveType) {
            return false;
        }
        if (pointIdentifier == null) {
            if (other.pointIdentifier != null) {
                return false;
            }
        } else if (!pointIdentifier.equals(other.pointIdentifier)) {
            return false;
        }
        if (stateGroupId != other.stateGroupId) {
            return false;
        }
        if (stateOneControl != other.stateOneControl) {
            return false;
        }
        if (stateZeroControl != other.stateZeroControl) {
            return false;
        }
        if (unitOfMeasure != other.unitOfMeasure) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append("pointIdentifier", pointIdentifier);
        builder.append("name", getName());
        builder.append("multiplier", multiplier);
        builder.append("dataOffset", dataOffset);
        builder.append("unitOfMeasure", unitOfMeasure);
        builder.append("stateGroupId", stateGroupId);
        builder.append("decimalPlaces", decimalPlaces);
        builder.append("pointArchiveInterval", pointArchiveInterval);
        builder.append("pointArchiveType", pointArchiveType);
        builder.append("initialState", initialState);
        builder.append("controlOffset", controlOffset);
        builder.append("controlType", controlType);
        builder.append("stateZeroControl", stateZeroControl);
        builder.append("stateOneControl", stateOneControl);
        if(calcPointInfo != null){
            builder.append("calcPointInfo", calcPointInfo);
        }
        return builder.toString();
    }
}
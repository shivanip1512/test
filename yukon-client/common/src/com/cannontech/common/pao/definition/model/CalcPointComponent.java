package com.cannontech.common.pao.definition.model;

import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.cannontech.database.db.point.calculation.CalcComponentTypes;

public class CalcPointComponent {

    private Integer pointId; // This is a convenience object used only for the creation and copying of new calculated points
    private PointIdentifier pointIdentifier;
    private String calcComponentType = CalcComponentTypes.OPERATION_COMP_TYPE;
    private String operation = CalcComponentTypes.ADDITION_OPERATION;

    public CalcPointComponent(PointIdentifier pointIdentifier, String calcComponentType, String operation) {
        this.pointIdentifier = pointIdentifier;
        this.calcComponentType = calcComponentType;
        this.operation = operation;
    }

    /**
     * This method should generally not be used and is currently only used during calculated point creation & copying
     */
    public Integer getPointId() {
        return pointId;
    }

    /**
     * This method should generally not be used and is currently only used during calculated point creation & copying
     */
    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }

    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }

    public void setPointIdentifier(PointIdentifier pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }

    public String getCalcComponentType() {
        return calcComponentType;
    }

    public void setCalcComponentType(String calcComponentType) {
        this.calcComponentType = calcComponentType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((calcComponentType == null) ? 0 : calcComponentType.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((pointId == null) ? 0 : pointId.hashCode());
        result = prime * result + ((pointIdentifier == null) ? 0 : pointIdentifier.hashCode());
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
        CalcPointComponent other = (CalcPointComponent) obj;
        if (calcComponentType == null) {
            if (other.calcComponentType != null) {
                return false;
            }
        } else if (!calcComponentType.equals(other.calcComponentType)) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (pointId == null) {
            if (other.pointId != null) {
                return false;
            }
        } else if (!pointId.equals(other.pointId)) {
            return false;
        }
        if (pointIdentifier == null) {
            if (other.pointIdentifier != null) {
                return false;
            }
        } else if (!pointIdentifier.equals(other.pointIdentifier)) {
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
        builder.append("calcComponentType", calcComponentType);
        builder.append("forceQualityNormal", operation);
        return builder.toString();
    }

}

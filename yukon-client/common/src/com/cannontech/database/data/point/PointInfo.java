package com.cannontech.database.data.point;

import com.cannontech.common.pao.definition.model.PointIdentifier;

public class PointInfo {
    private int pointId;
    private String name;
    private PointIdentifier pointIdentifier;
    private String unitOfMeasure;
    private int stateGroupId;

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PointType getType() {
        return pointIdentifier.getPointType();
    }

    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }

    public void setPointIdentifier(PointIdentifier pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public int getStateGroupId() {
        return stateGroupId;
    }

    public void setStateGroupId(int stateGroupId) {
        this.stateGroupId = stateGroupId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pointId;
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
        PointInfo other = (PointInfo) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (pointId != other.pointId)
            return false;
        if (pointIdentifier == null) {
            if (other.pointIdentifier != null)
                return false;
        } else if (!pointIdentifier.equals(other.pointIdentifier))
            return false;
        if (stateGroupId != other.stateGroupId)
            return false;
        if (unitOfMeasure == null) {
            if (other.unitOfMeasure != null)
                return false;
        } else if (!unitOfMeasure.equals(other.unitOfMeasure))
            return false;
        return true;
    }
}

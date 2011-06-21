package com.cannontech.database.data.point;

public class PointInfo {
    private String name;
    private PointType type;
    private String unitOfMeasure;
    private int stateGroupId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PointType getType() {
        return type;
    }

    public void setType(PointType type) {
        this.type = type;
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + stateGroupId;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((unitOfMeasure == null) ? 0 : unitOfMeasure.hashCode());
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
        if (stateGroupId != other.stateGroupId)
            return false;
        if (type != other.type)
            return false;
        if (unitOfMeasure == null) {
            if (other.unitOfMeasure != null)
                return false;
        } else if (!unitOfMeasure.equals(other.unitOfMeasure))
            return false;
        return true;
    }
}

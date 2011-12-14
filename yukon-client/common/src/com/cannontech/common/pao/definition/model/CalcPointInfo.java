package com.cannontech.common.pao.definition.model;

import java.util.List;

import com.cannontech.database.db.point.calculation.CalcComponentTypes;

public class CalcPointInfo {

    private String updateType = CalcComponentTypes.CONSTANT_COMP_TYPE;
    private int periodicRate = 0;
    private boolean forceQualityNormal = false;
    private List<CalcPointComponent> components;

    public CalcPointInfo(String updateType, int periodicRate, boolean forceQualityNormal) {
        this.updateType = updateType;
        this.periodicRate = periodicRate;
        this.forceQualityNormal = forceQualityNormal;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public int getPeriodicRate() {
        return periodicRate;
    }

    public void setPeriodicRate(int periodicRate) {
        this.periodicRate = periodicRate;
    }

    public boolean isForceQualityNormal() {
        return forceQualityNormal;
    }

    public void setForceQualityNormal(boolean forceQualityNormal) {
        this.forceQualityNormal = forceQualityNormal;
    }

    public List<CalcPointComponent> getComponents() {
        return components;
    }

    public void setComponents(List<CalcPointComponent> components) {
        this.components = components;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((components == null) ? 0 : components.hashCode());
        result = prime * result + (forceQualityNormal ? 1231 : 1237);
        result = prime * result + periodicRate;
        result = prime * result + ((updateType == null) ? 0 : updateType.hashCode());
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
        CalcPointInfo other = (CalcPointInfo) obj;
        if (components == null) {
            if (other.components != null)
                return false;
        } else if (!components.equals(other.components))
            return false;
        if (forceQualityNormal != other.forceQualityNormal)
            return false;
        if (periodicRate != other.periodicRate)
            return false;
        if (updateType == null) {
            if (other.updateType != null)
                return false;
        } else if (!updateType.equals(other.updateType))
            return false;
        return true;
    }

}

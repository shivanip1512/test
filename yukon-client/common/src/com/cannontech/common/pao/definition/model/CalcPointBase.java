package com.cannontech.common.pao.definition.model;

import java.util.List;

import com.cannontech.database.db.point.calculation.CalcComponentTypes;

public class CalcPointBase {

    private String updateType = CalcComponentTypes.CONSTANT_COMP_TYPE;
    private int periodicRate;
    private boolean forceQualityNormal;
    private List<CalcPointComponent> components;

    public CalcPointBase(String updateType, int periodicRate, boolean forceQualityNormal) {
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

}

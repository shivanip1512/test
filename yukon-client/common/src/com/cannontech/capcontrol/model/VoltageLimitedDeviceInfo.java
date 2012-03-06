package com.cannontech.capcontrol.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.enums.Phase;

public class VoltageLimitedDeviceInfo {
    private PaoIdentifier parentPaoIdentifier;
    private String paoName;
    private int pointId;
    private String pointName;
    private Phase phase;
    private double lowerLimit;
    private double upperLimit;
    private boolean overrideStrategy;

    public PaoIdentifier getParentPaoIdentifier() {
        return parentPaoIdentifier;
    }

    public void setParentPaoIdentifier(PaoIdentifier parentPaoIdentifier) {
        this.parentPaoIdentifier = parentPaoIdentifier;
    }

    public String getPaoName() {
        return paoName;
    }

    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public boolean isOverrideStrategy() {
        return overrideStrategy;
    }

    public void setOverrideStrategy(boolean overrideStrategy) {
        this.overrideStrategy = overrideStrategy;
    }

}

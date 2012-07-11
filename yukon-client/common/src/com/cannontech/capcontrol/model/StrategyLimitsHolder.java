package com.cannontech.capcontrol.model;

import com.cannontech.database.db.capcontrol.CapControlStrategy;

public class StrategyLimitsHolder {
    private CapControlStrategy strategy;
    private double upperLimit;
    private double lowerLimit;

    public StrategyLimitsHolder(CapControlStrategy strategy, double upperLimit, double lowerLimit) {
        this.strategy = strategy;
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
    }

    public CapControlStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(CapControlStrategy strategy) {
        this.strategy = strategy;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

}

package com.cannontech.database.db.capcontrol;

public class PowerFactorCorrectionSetting {
    private double bandwidth = PowerFactorCorrectionSettingType.BANDWIDTH.getDefaultValue();
    private double cost = PowerFactorCorrectionSettingType.COST.getDefaultValue();
    private double maxCost = PowerFactorCorrectionSettingType.MAX_COST.getDefaultValue();

    public PowerFactorCorrectionSetting() {}

    public PowerFactorCorrectionSetting(double bandwidth,
                                        double cost, double maxCost) {
        this.bandwidth = bandwidth;
        this.cost = cost;
        this.maxCost = maxCost;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(double maxCost) {
        this.maxCost = maxCost;
    }

}

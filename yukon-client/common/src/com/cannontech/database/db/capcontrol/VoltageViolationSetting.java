package com.cannontech.database.db.capcontrol;

public class VoltageViolationSetting {
    private double bandwidth;
    private double cost;
    private double emergencyCost;
    
    public VoltageViolationSetting() {
        this.bandwidth = 0.2;
        this.cost = 0;
        this.emergencyCost = 0;
    }

    public VoltageViolationSetting(double bandwidth, double cost, double emergencyCost) {
        this.bandwidth = bandwidth;
        this.cost = cost;
        this.emergencyCost = emergencyCost;
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

    public double getEmergencyCost() {
        return emergencyCost;
    }

    public void setEmergencyCost(double emergencyCost) {
        this.emergencyCost = emergencyCost;
    }

}

package com.cannontech.database.db.capcontrol;

public class VoltageViolationSetting {
    private VoltageViolationSettingNameType name;
    private double bandwidth;
    private double cost;
    private double emergencyCost;

    public VoltageViolationSetting(VoltageViolationSettingNameType name, double bandwidth,
                                    double cost, double emergencyCost) {
        this.name = name;
        this.bandwidth = bandwidth;
        this.cost = cost;
        this.emergencyCost = emergencyCost;
    }

    public VoltageViolationSettingNameType getName() {
        return name;
    }

    public void setName(VoltageViolationSettingNameType name) {
        this.name = name;
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

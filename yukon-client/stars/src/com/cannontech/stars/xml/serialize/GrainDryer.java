package com.cannontech.stars.xml.serialize;

public class GrainDryer {
    private DryerType dryerType;
    private BinSize binSize;
    private BlowerEnergySource blowerEnergySource;
    private BlowerHorsePower blowerHorsePower;
    private BlowerHeatSource blowerHeatSource;

    public GrainDryer() {
        this.dryerType = new DryerType();
        this.binSize = new BinSize();
        this.blowerEnergySource = new BlowerEnergySource();
        this.blowerHorsePower = new BlowerHorsePower();
        this.blowerHeatSource = new BlowerHeatSource();
    }

    public BinSize getBinSize() {
        return this.binSize;
    }

    public BlowerEnergySource getBlowerEnergySource() {
        return this.blowerEnergySource;
    }

    public BlowerHeatSource getBlowerHeatSource() {
        return this.blowerHeatSource;
    }

    public BlowerHorsePower getBlowerHorsePower() {
        return this.blowerHorsePower;
    }

    public DryerType getDryerType() {
        return this.dryerType;
    }

    public void setBinSize(BinSize binSize) {
        this.binSize = binSize;
    }

    public void setBlowerEnergySource(BlowerEnergySource blowerEnergySource) {
        this.blowerEnergySource = blowerEnergySource;
    }

    public void setBlowerHeatSource(BlowerHeatSource blowerHeatSource) {
        this.blowerHeatSource = blowerHeatSource;
    }

    public void setBlowerHorsePower(BlowerHorsePower blowerHorsePower) {
        this.blowerHorsePower = blowerHorsePower;
    }

    public void setDryerType(DryerType dryerType) {
        this.dryerType = dryerType;
    }

}

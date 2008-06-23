package com.cannontech.stars.xml.serialize;

public class Irrigation {
    private IrrigationType irrigationType;
    private HorsePower horsePower;
    private EnergySource energySource;
    private SoilType soilType;
    private MeterLocation meterLocation;
    private MeterVoltage meterVoltage;

    public Irrigation() {

    }

    public EnergySource getEnergySource() {
        return this.energySource;
    }

    public HorsePower getHorsePower() {
        return this.horsePower;
    }

    public IrrigationType getIrrigationType() {
        return this.irrigationType;
    }

    public MeterLocation getMeterLocation() {
        return this.meterLocation;
    }

    public MeterVoltage getMeterVoltage() {
        return this.meterVoltage;
    }

    public SoilType getSoilType() {
        return this.soilType;
    }

    public void setEnergySource(EnergySource energySource) {
        this.energySource = energySource;
    }

    public void setHorsePower(HorsePower horsePower) {
        this.horsePower = horsePower;
    }

    public void setIrrigationType(IrrigationType irrigationType) {
        this.irrigationType = irrigationType;
    }

    public void setMeterLocation(MeterLocation meterLocation) {
        this.meterLocation = meterLocation;
    }

    public void setMeterVoltage(MeterVoltage meterVoltage) {
        this.meterVoltage = meterVoltage;
    }

    public void setSoilType(SoilType soilType) {
        this.soilType = soilType;
    }

}

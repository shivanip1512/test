package com.cannontech.cbc.cyme.model;

public class PhaseInformation {
    private float current;
    private float voltage;
    private float kVar;
    private float kW;
    private float tapPosition;

    public PhaseInformation(float current, float voltage, float kVar, float kW, float tapPosition) {
        this.current = current;
        this.voltage = voltage;
        this.kVar = kVar;
        this.kW = kW;
        this.tapPosition = tapPosition;
    }
    
    public void setCurrent(float current) {
        this.current = current;
    }
    public float getCurrent() {
        return current;
    }
    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }
    public float getVoltage() {
        return voltage;
    }
    public void setkVar(float kVar) {
        this.kVar = kVar;
    }
    public float getkVar() {
        return kVar;
    }
    public void setkW(float kW) {
        this.kW = kW;
    }
    public float getkW() {
        return kW;
    }
    public float getTapPosition() {
        return tapPosition;
    }
    public void setTapPosition(float tapPosition) {
        this.tapPosition = tapPosition;
    }    
};

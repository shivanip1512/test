package com.cannontech.cbc.cyme.model;

public class PhaseInformation {
    private float current;
    private float voltage;
    private float kVar;
    private float kW;
    private float tapPosition;
    private float voltageSetPoint;

    public PhaseInformation(float current, float voltage, float kVar, float kW, float tapPosition, float voltageSetPoint) {
        this.current = current;
        this.voltage = voltage;
        this.kVar = kVar;
        this.kW = kW;
        this.tapPosition = tapPosition;
        this.voltageSetPoint = voltageSetPoint;
    }

    public float getCurrent() {
        return current;
    }

    public float getVoltage() {
        return voltage;
    }

    public float getkVar() {
        return kVar;
    }

    public float getkW() {
        return kW;
    }

    public float getTapPosition() {
        return tapPosition;
    }

    public float getVoltageSetPoint() {
        return voltageSetPoint;
    }
};

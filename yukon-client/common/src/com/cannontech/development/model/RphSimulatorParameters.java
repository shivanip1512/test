package com.cannontech.development.model;

import org.joda.time.Instant;
import org.joda.time.Period;

public class RphSimulatorParameters {

    private String deviceGroupName;
    private Instant start;
    private Instant stop;
    private Period period;
    private double valueLow;
    private double valueHigh;
    private String type;

    public String getDeviceGroupName() {
        return deviceGroupName;
    }

    public void setDeviceGroupName(String deviceGroupName) {
        this.deviceGroupName = deviceGroupName;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getStop() {
        return stop;
    }

    public void setStop(Instant stop) {
        this.stop = stop;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public double getValueLow() {
        return valueLow;
    }

    public void setValueLow(double valueLow) {
        this.valueLow = valueLow;
    }

    public double getValueHigh() {
        return valueHigh;
    }

    public void setValueHigh(double valueHigh) {
        this.valueHigh = valueHigh;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

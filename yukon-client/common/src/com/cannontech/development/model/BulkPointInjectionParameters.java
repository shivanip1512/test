package com.cannontech.development.model;

import org.joda.time.Instant;
import org.joda.time.Period;

import com.cannontech.common.pao.attribute.model.Attribute;

public class BulkPointInjectionParameters {

    private String deviceGroupName;
    private Attribute fullAttribute;
    private Attribute partialAttribute;
    private Instant start;
    private Instant stop;
    private Period period;
    private double valueLow;
    private double valueHigh;
    private boolean fullSupport;
    private int throttlePerSecond;

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

    public void setFullAttribute(Attribute fullAttribute) {
        this.fullAttribute = fullAttribute;
    }

    public void setPartialAttribute(Attribute partialAttribute) {
        this.partialAttribute = partialAttribute;
    }

    public Attribute getAttribute() {
        return fullSupport ? fullAttribute : partialAttribute;
    }

    public boolean isFullSupport() {
        return fullSupport;
    }

    public void setFullSupport(boolean fullSupport) {
        this.fullSupport = fullSupport;
    }

    public int getThrottlePerSecond() {
        return throttlePerSecond;
    }

    public void setThrottlePerSecond(int throttlePerSecond) {
        this.throttlePerSecond = throttlePerSecond;
    }
}

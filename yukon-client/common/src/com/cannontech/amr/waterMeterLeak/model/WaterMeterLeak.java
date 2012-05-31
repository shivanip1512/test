package com.cannontech.amr.waterMeterLeak.model;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.core.dynamic.PointValueHolder;

public class WaterMeterLeak {

    private Meter meter;
    private PointValueHolder pointValueHolder;
    private double leakRate;

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public PointValueHolder getPointValueHolder() {
        return pointValueHolder;
    }

    public void setPointValueHolder(PointValueHolder pointValueHolder) {
        this.pointValueHolder = pointValueHolder;
    }

    public double getLeakRate() {
        return leakRate;
    }

    public void setLeakRate(double leakRate) {
        this.leakRate = leakRate;
    }

}

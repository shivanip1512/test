package com.cannontech.web.amr.waterLeakReport.model;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.core.dynamic.PointValueHolder;

public class WaterMeterLeak {

    private YukonMeter meter;
    private PointValueHolder pointValueHolder;
    private double leakRate;

    public YukonMeter getMeter() {
        return meter;
    }
    
    public void setMeter(YukonMeter meter) {
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

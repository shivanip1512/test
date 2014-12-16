package com.cannontech.database.data.point;

import com.cannontech.database.data.lite.LitePoint;

public class CapBankMonitorPointParams {

private LitePoint monitorPoint = null;

private int deviceId;
private int displayOrder;
private long NINAvg = 3;
private double upperBandwidth;
private double lowerBandwidth;
private boolean overrideFdrLimits;
private boolean initScan;
private String phase;

    public CapBankMonitorPointParams(){
        super();
    }

    public CapBankMonitorPointParams(LitePoint litePoint) {
        super();
        monitorPoint = litePoint;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int _deviceId_) {
        deviceId = _deviceId_;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public double getLowerBandwidth() {
        return lowerBandwidth;
    }

    public void setLowerBandwidth(double lowerBandwidth) {
        this.lowerBandwidth = lowerBandwidth;
    }

    public long getNINAvg() {
        return NINAvg;
    }

    public void setNINAvg(long avg) {
        NINAvg = avg;
    }

    public int getPointId() {
        if (monitorPoint != null)
            return monitorPoint.getPointID();
        else
            return 0;
    }

    public void setPointId(int pointId) {
        if (monitorPoint == null)
            monitorPoint = new LitePoint ( pointId );
        else
            monitorPoint.setPointID(pointId);
    }

    public double getUpperBandwidth() {
        return upperBandwidth;
    }

    public void setUpperBandwidth(double upperBandwidth) {
        this.upperBandwidth = upperBandwidth;
    }

    public String getPointName() {
        if (monitorPoint != null)
            return monitorPoint.getPointName();
        return "";
    }

    public void setPointName(String pointName) {
        if (monitorPoint != null)
            monitorPoint.setPointName( pointName );
        else
            monitorPoint.setPointName("");
    }

    public boolean isOverrideFdrLimits() {
        return overrideFdrLimits;
    }

    public void setOverrideFdrLimits(boolean overrideFdrLimits) {
        this.overrideFdrLimits = overrideFdrLimits;
    }

    public boolean isInitScan() {
        return initScan;
    }

    public void setInitScan(boolean initScan) {
        this.initScan = initScan;
    }


    public void setMonitorPoint(LitePoint monitorPoint) {
        this.monitorPoint = monitorPoint;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

}
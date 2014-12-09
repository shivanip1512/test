package com.cannontech.web.capcontrol.models;

import com.cannontech.database.data.lite.LitePoint;

public class BusDetails {

    private LitePoint varPoint;
    private LitePoint wattPoint;
    private LitePoint voltPoint;

    public BusDetails() {
    }

    public final LitePoint getVarPoint() {
        return varPoint;
    }
    public final void setVarPoint(LitePoint varPoint) {
        this.varPoint = varPoint;
    }
    public final LitePoint getWattPoint() {
        return wattPoint;
    }
    public final void setWattPoint(LitePoint wattPoint) {
        this.wattPoint = wattPoint;
    }
    public final LitePoint getVoltPoint() {
        return voltPoint;
    }
    public final void setVoltPoint(LitePoint voltPoint) {
        this.voltPoint = voltPoint;
    }
}
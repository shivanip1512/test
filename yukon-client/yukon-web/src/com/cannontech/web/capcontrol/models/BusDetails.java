package com.cannontech.web.capcontrol.models;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.capcontrol.streamable.SubBus;

public class BusDetails {

    private LitePoint varPoint;
    private LitePoint wattPoint;
    private LitePoint voltPoint;
    private ControlMethod controlMethod;
    private ControlAlgorithm controlUnits;

    public BusDetails() {
    }

    public BusDetails(SubBus bus) {
        controlMethod = bus.getControlMethod();
        controlUnits = bus.getControlUnits();
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
    public final ControlMethod getControlMethod() {
        return controlMethod;
    }
    public final void setControlMethod(ControlMethod controlMethod) {
        this.controlMethod = controlMethod;
    }
    public final ControlAlgorithm getControlUnits() {
        return controlUnits;
    }
    public final void setControlUnits(ControlAlgorithm controlUnits) {
        this.controlUnits = controlUnits;
    }
}
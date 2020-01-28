package com.cannontech.loadcontrol.gear.model;

import com.cannontech.database.data.device.lm.HeatCool;

public class HoneywellSetpointValues {
    private Integer setpointOffset;
    private Integer precoolOffset;
    private HeatCool heatCool;

    public HoneywellSetpointValues(Integer setpointOffset, HeatCool heatCool, Integer precoolOffset) {
        this.setpointOffset = setpointOffset;
        this.precoolOffset = precoolOffset;
        this.heatCool = heatCool;
    }

    public Integer getSetpointOffset() {
        return setpointOffset;
    }

    public Integer getPrecoolOffset() {
        return precoolOffset;
    }

    public HeatCool getHeatCool() {
        return heatCool;
    }

}

package com.cannontech.loadcontrol.gear.model;

import com.cannontech.database.data.device.lm.HeatCool;

public class HoneywellSetpointValues {
    private Integer setpointOffset;
    private HeatCool heatCool;

    public HoneywellSetpointValues(Integer setpointOffset, HeatCool heatCool) {
        this.setpointOffset = setpointOffset;
        this.heatCool = heatCool;
    }

    public Integer getSetpointOffset() {
        return setpointOffset;
    }

    public HeatCool getHeatCool() {
        return heatCool;
    }

}

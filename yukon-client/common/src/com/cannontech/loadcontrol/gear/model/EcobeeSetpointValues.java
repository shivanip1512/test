package com.cannontech.loadcontrol.gear.model;

import com.cannontech.database.data.device.lm.HeatCool;

public class EcobeeSetpointValues {
    private Integer setpointOffset;
    private HeatCool heatCool;

    public EcobeeSetpointValues(Integer setpointOffset, HeatCool heatCool) {
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

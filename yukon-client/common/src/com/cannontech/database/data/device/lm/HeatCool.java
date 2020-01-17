package com.cannontech.database.data.device.lm;

import com.cannontech.common.dr.gear.setup.Mode;

/**
 * Enum to map between LmThermostatGear.Settings and Heat/Cool UI selector
 */
public enum HeatCool {
    HEAT("--H-"),
    COOL("---I");
    
    private String dbValue;
    
    private HeatCool(String dbValue) {
        this.dbValue = dbValue;
    }
    
    public String getDbValue() {
        return dbValue;
    }
    
    public Mode getMode() {
        if(this == HEAT) {
            return Mode.HEAT;
        }
        return Mode.COOL;
    }
    
    public static HeatCool fromMode(Mode mode) {
        if(mode == Mode.HEAT) {
            return HEAT;
        }
        return COOL;
    }
    
    public static HeatCool of(Object dbValue) {
        if (HEAT.dbValue.equals(dbValue)) {
            return HEAT;
        }
        return COOL;
    }
}
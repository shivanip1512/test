package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.LMThermostatGear;

public class SimpleThermostatRampingGear extends LMThermostatGear {

    public SimpleThermostatRampingGear() {
        setControlMethod(SIMPLE_THERMOSTAT_SETBACK);
        setGearID(super.getGearID());
    }
    
}

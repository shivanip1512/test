package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMThermostatGear;

public class SimpleThermostatRampingGear extends LMThermostatGear {

    public SimpleThermostatRampingGear() {
        setControlMethod(GearControlMethod.SimpleThermostatRamping);
        setGearID(super.getGearID());
    }
    
}

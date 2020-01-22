package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class MeterDisconnectGear extends LMProgramDirectGear {

    public MeterDisconnectGear() {
        setControlMethod(GearControlMethod.MeterDisconnect);
    }
    
    @Override
    public boolean useCustomDbRetrieve() {
        return false;
    }
}

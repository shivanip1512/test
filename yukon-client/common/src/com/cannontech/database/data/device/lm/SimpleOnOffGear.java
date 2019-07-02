package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class SimpleOnOffGear extends LMProgramDirectGear {

    public SimpleOnOffGear() {
        setControlMethod(GearControlMethod.SimpleOnOff);
    }
}

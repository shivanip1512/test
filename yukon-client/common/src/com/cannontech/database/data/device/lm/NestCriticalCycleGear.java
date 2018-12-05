package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMNestGear;

public class NestCriticalCycleGear extends LMNestGear {

    public NestCriticalCycleGear() {
        setControlMethod(GearControlMethod.NestCriticalCycle);
        setGearID(super.getGearID());
    }
}
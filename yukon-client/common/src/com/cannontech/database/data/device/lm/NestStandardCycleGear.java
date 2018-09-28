package com.cannontech.database.data.device.lm;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class NestStandardCycleGear extends com.cannontech.database.db.device.lm.LMNestGear {

    private static final long serialVersionUID = 1L;

    public NestStandardCycleGear() {
        setControlMethod(GearControlMethod.NestStandardCycle);
        setGearID(super.getGearID());
    }
}

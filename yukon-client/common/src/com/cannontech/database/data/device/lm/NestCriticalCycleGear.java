package com.cannontech.database.data.device.lm;

import java.sql.SQLException;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class NestCriticalCycleGear extends com.cannontech.database.db.device.lm.LMProgramDirectGear {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME_NEST = "LMNestLoadShapingGear";

    public NestCriticalCycleGear() {
        setControlMethod(GearControlMethod.NestCriticalCycle);
        setGearID(super.getGearID());
    }

    @Override
    public void update() throws SQLException {
        super.update();
        delete(TABLE_NAME_NEST, "GearID", getGearID());
    }
}

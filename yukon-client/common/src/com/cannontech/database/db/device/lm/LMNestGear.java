package com.cannontech.database.db.device.lm;

public abstract class LMNestGear extends LMProgramDirectGear {

    public LMNestGear() {
        super();
    }
    
    @Override
    public boolean useCustomDbRetrieve() {
        return true;
    }
}
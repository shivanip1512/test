package com.cannontech.core.dao;

import com.cannontech.loadcontrol.gear.model.TierGearContainer;

public interface LMGearDao {
    
    public void insertContainer(TierGearContainer tgc);
    public void updateContainer(TierGearContainer tgc);
    public TierGearContainer getContainer(int gearId);
    public void delete(int gearId);
    
}
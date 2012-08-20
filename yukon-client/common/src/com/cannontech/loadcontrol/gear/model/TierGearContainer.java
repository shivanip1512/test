package com.cannontech.loadcontrol.gear.model;

import java.io.Serializable;

public class TierGearContainer implements Serializable{
    private Integer gearId;
    private Integer tier;
    
    public Integer getGearId() {
        return gearId;
    }
    
    public void setGearId(Integer gearId) {
        this.gearId = gearId;
    }
   
    public Integer  getTier() {
        return tier;
    }
    
    public void setTier(Integer tier) {
        this.tier = tier;
    }
}

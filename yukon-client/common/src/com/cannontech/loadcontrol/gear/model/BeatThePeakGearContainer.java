package com.cannontech.loadcontrol.gear.model;

import java.io.Serializable;

public class BeatThePeakGearContainer implements Serializable{
    private Integer gearId;
    private String alertLevel;
    
    public Integer getGearId() {
        return gearId;
    }
    
    public void setGearId(Integer gearId) {
        this.gearId = gearId;
    }
   
    public String  getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }
}

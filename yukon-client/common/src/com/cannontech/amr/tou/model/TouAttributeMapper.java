package com.cannontech.amr.tou.model;

import com.cannontech.common.device.attribute.model.Attribute;

public class TouAttributeMapper {
    private int touId;
    private String displayName;
    private Attribute usage;
    private Attribute peak;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Attribute getPeak() {
        return peak;
    }

    public void setPeak(Attribute peak) {
        this.peak = peak;
    }

    public Attribute getUsage() {
        return usage;
    }

    public void setUsage(Attribute usage) {
        this.usage = usage;
    }

    public int getTouId() {
        return touId;
    }

    public void setTouId(int touId) {
        this.touId = touId;
    }


}

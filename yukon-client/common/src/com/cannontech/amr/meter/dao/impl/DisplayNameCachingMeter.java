package com.cannontech.amr.meter.dao.impl;

import com.cannontech.amr.meter.model.Meter;

public class DisplayNameCachingMeter extends Meter {
    private String displayNameCache = null;

    public String getDisplayNameCache() {
        return displayNameCache;
    }

    public void setDisplayNameCache(String displayNameCache) {
        this.displayNameCache = displayNameCache;
    }

}

package com.cannontech.dr.assetavailability;

import org.joda.time.Instant;

public final class DisplayableApplianceWithRuntime {
    final int applianceCategoryId;
    final Instant lastNonZeroRuntimeTime;
    
    public DisplayableApplianceWithRuntime(int applianceCategoryId, Instant lastNonZeroRuntimeTime) {
        this.applianceCategoryId = applianceCategoryId;
        this.lastNonZeroRuntimeTime = lastNonZeroRuntimeTime;
    }

    public int getApplianceCategoryId() {
        return applianceCategoryId;
    }

    public Instant getLastNonZeroRuntime() {
        return lastNonZeroRuntimeTime;
    }
}

package com.cannontech.dr.assetavailability;

import org.joda.time.Instant;

/**
 * Object representing an appliance, including the applianceCategoryId and the most recent Instant that non-zero
 * runtime was recorded.
 */
public final class ApplianceWithRuntime {
    //applianceCategoryId is used to get appliance category display name
    final int applianceCategoryId;
    final Instant lastNonZeroRuntimeTime;
    
    public ApplianceWithRuntime(int applianceCategoryId, Instant lastNonZeroRuntimeTime) {
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

package com.cannontech.dr.assetavailability;

import org.joda.time.Instant;

/**
 * Object representing an appliance, including the applianceCategoryId and the most recent Instant that non-zero
 * runtime was recorded.
 */
public final class ApplianceWithRuntime {
    //applianceCategoryId is used to get appliance category display name
    final private int applianceCategoryId;
    final private Instant lastNonZeroRuntimeTime;
    
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + applianceCategoryId;
        result =
            prime * result
                    + ((lastNonZeroRuntimeTime == null) ? 0 : lastNonZeroRuntimeTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApplianceWithRuntime other = (ApplianceWithRuntime) obj;
        if (applianceCategoryId != other.applianceCategoryId)
            return false;
        if (lastNonZeroRuntimeTime == null) {
            if (other.lastNonZeroRuntimeTime != null)
                return false;
        } else if (!lastNonZeroRuntimeTime.equals(other.lastNonZeroRuntimeTime))
            return false;
        return true;
    }
}

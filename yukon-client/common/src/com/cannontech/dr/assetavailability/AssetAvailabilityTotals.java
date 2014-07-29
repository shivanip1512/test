package com.cannontech.dr.assetavailability;

public class AssetAvailabilityTotals {
    private final int active;
    private final int inactive;
    private final int unavailable;
    
    public AssetAvailabilityTotals(int active, int inactive, int unavailable) {
        this.active = active;
        this.inactive = inactive;
        this.unavailable = unavailable;
    }

    public int getActive() {
        return active;
    }

    public int getInactive() {
        return inactive;
    }

    public int getUnavailable() {
        return unavailable;
    }
}

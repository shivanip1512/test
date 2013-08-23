package com.cannontech.dr.assetavailability;

import java.util.Collection;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableSet;

/**
 * Contains asset availability information for a single piece of inventory.
 */
public final class SimpleAssetAvailability {
    private final int inventoryId;
    private final AssetAvailabilityStatus status;
    private final boolean isOptedOut;
    private final Instant lastCommunicationTime;
    private final ImmutableSet<ApplianceWithRuntime> applianceRuntimes;
    
    public SimpleAssetAvailability(int inventoryId, AssetAvailabilityStatus status, boolean isOptedOut,
                              Instant lastCommunicationTime,
                              Collection<ApplianceWithRuntime> applianceRuntimes) {
        this.inventoryId = inventoryId;
        this.status = status;
        this.isOptedOut = isOptedOut;
        this.lastCommunicationTime = lastCommunicationTime;
        this.applianceRuntimes = ImmutableSet.copyOf(applianceRuntimes);
    }
    
    /**
     * Constructor for one-way inventory (which are always considered to be in communication &
     * running). Communication time and runtime will be set to null.
     */
    public SimpleAssetAvailability(int inventoryId, boolean isOptedOut) {
        this.inventoryId = inventoryId;
        this.status = AssetAvailabilityStatus.IN_COMMUNICATION_RUNNING;
        this.isOptedOut = isOptedOut;
        this.lastCommunicationTime = null;
        this.applianceRuntimes = null;
    }
    
    public int getInventoryId() {
        return inventoryId;
    }

    public AssetAvailabilityStatus getStatus() {
        return status;
    }

    public AssetAvailabilityCombinedStatus getCombinedStatus() {
        return AssetAvailabilityCombinedStatus.valueOf(status, isOptedOut);
    }

    public boolean isOptedOut() {
        return isOptedOut;
    }
    
    /**
     * Can be null if this inventory is one-way, or if it has never communicated.
     */
    public Instant getLastCommunicationTime() {
        return lastCommunicationTime;
    }
    
    /**
     * Can be null if this inventory is one-way, or empty if no non-zero runtime has been reported.
     */
    public ImmutableSet<ApplianceWithRuntime> getApplianceRuntimes() {
        return applianceRuntimes;
    }
}

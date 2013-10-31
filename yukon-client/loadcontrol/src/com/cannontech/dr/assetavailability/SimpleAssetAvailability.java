package com.cannontech.dr.assetavailability;

import java.util.Collection;
import java.util.Set;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

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
     * running). Communication time and runtime (but not applianceCategoryIds) will be set to null.
     */
    public SimpleAssetAvailability(int inventoryId, boolean isOptedOut, Iterable<Integer> appliances) {
        this.inventoryId = inventoryId;
        this.status = AssetAvailabilityStatus.ACTIVE;
        this.isOptedOut = isOptedOut;
        this.lastCommunicationTime = null;
        Set<ApplianceWithRuntime> appliancesWithoutRuntime = Sets.newHashSet();
        for(Integer applianceCategoryId : appliances) {
            ApplianceWithRuntime applianceWithoutRuntime = new ApplianceWithRuntime(applianceCategoryId, null);
            appliancesWithoutRuntime.add(applianceWithoutRuntime);
        }
        this.applianceRuntimes = ImmutableSet.copyOf(appliancesWithoutRuntime);
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
     * Runtime can be null if no non-zero runtime has been reported, if this inventory is one-way.
     */
    public ImmutableSet<ApplianceWithRuntime> getApplianceRuntimes() {
        return applianceRuntimes;
    }
}

package com.cannontech.dr.assetavailability;

import java.util.List;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class AssetAvailability {
    private final int deviceId;
    private final AssetAvailabilityStatus status;
    private final boolean isOptedOut;
    private final Instant lastCommunicationTime;
    private final ImmutableList<ApplianceWithRuntime> appliances; 
    
    public AssetAvailability(int deviceId, AssetAvailabilityStatus status, boolean isOptedOut, Instant lastCommunicationTime, List<ApplianceWithRuntime> appliances) {
        this.deviceId = deviceId;
        this.status = status;
        this.isOptedOut = isOptedOut;
        this.lastCommunicationTime = lastCommunicationTime;
        this.appliances = ImmutableList.copyOf(appliances);
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public AssetAvailabilityStatus getStatus() {
        return status;
    }
    
    public boolean isOptedOut() {
        return isOptedOut;
    }
    
    public Instant getLastCommunicationTime() {
        return lastCommunicationTime;
    }
    
    public List<ApplianceWithRuntime> getAppliancesWithRuntime() {
        return Lists.newArrayList(appliances);
    }
    
    public static AssetAvailability getEmptyAvailability(int deviceId) {
        return new AssetAvailability(deviceId, null, false, null, Lists.newArrayList(new ApplianceWithRuntime(null, ApplianceRuntime.NONE)));
    }
}

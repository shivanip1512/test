package com.cannontech.dr.assetavailability;

import java.util.List;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class AssetAvailability implements Comparable<AssetAvailability> {
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
    
    //TODO is this adequate?
    @Override
    public int compareTo(AssetAvailability other) {
        return Integer.compare(deviceId, other.deviceId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appliances == null) ? 0 : appliances.hashCode());
        result = prime * result + deviceId;
        result = prime * result + (isOptedOut ? 1231 : 1237);
        result = prime * result
                    + ((lastCommunicationTime == null) ? 0 : lastCommunicationTime.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        AssetAvailability other = (AssetAvailability) obj;
        if (appliances == null) {
            if (other.appliances != null)
                return false;
        } else if (!appliances.equals(other.appliances))
            return false;
        if (deviceId != other.deviceId)
            return false;
        if (isOptedOut != other.isOptedOut)
            return false;
        if (lastCommunicationTime == null) {
            if (other.lastCommunicationTime != null)
                return false;
        } else if (!lastCommunicationTime.equals(other.lastCommunicationTime))
            return false;
        if (status != other.status)
            return false;
        return true;
    }
}

package com.cannontech.dr.assetavailability;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

public class AssetAvailabilityPointDataTimes {
    private final int deviceId;
    private Instant lastCommunicationTime;
    private Map<Integer, Instant> relayRuntimes = new HashMap<>();
    
    public AssetAvailabilityPointDataTimes(int deviceId) {
        this.deviceId = deviceId;
    }

    public Instant getLastCommunicationTime() {
        return lastCommunicationTime;
    }

    public void setLastCommunicationTime(Instant lastCommunicationTime) {
        this.lastCommunicationTime = lastCommunicationTime;
    }

    public Instant getLastNonZeroRuntime() {
        if(relayRuntimes == null) return null;
        
        Instant lastNonZeroRuntime = null;
        for (Map.Entry<Integer, Instant> entry : relayRuntimes.entrySet()) {
            Instant time = entry.getValue();
            if (time != null) {
                lastNonZeroRuntime = time;
            }
        }
        
        return lastNonZeroRuntime;
    }

    public Map<Integer, Instant> getRelayRuntimes() {
        return relayRuntimes;
    }
    
    public Instant getRelayRuntime(int relay) {
        return relayRuntimes.get(relay);
    }
    
    public void setRelayRuntimes(Map<Integer, Instant> relayRuntimes) {
        this.relayRuntimes = relayRuntimes;
    }
    
    public void setRelayRuntime(int relay, Instant time) {
        relayRuntimes.put(relay, time);
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }
}

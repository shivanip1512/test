package com.cannontech.dr.assetavailability;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.Instant;

import com.google.common.collect.Maps;

public final class AllRelayCommunicationTimes extends DeviceCommunicationTimes {
    private final Map<Integer, Instant> relayRuntimes = new HashMap<>();
    
    public AllRelayCommunicationTimes(Instant lastCommunicationTime, Instant lastNonZeroRuntime, 
            Instant relay1Runtime, Instant relay2Runtime, Instant relay3Runtime, Instant relay4Runtime) {
        super(lastCommunicationTime, lastNonZeroRuntime);
        this.relayRuntimes.put(1, relay1Runtime);
        this.relayRuntimes.put(2, relay2Runtime);
        this.relayRuntimes.put(3, relay3Runtime);
        this.relayRuntimes.put(4, relay4Runtime);
    }
    
    public Instant getRelayRuntime(int relay) {
        if(!relayRuntimes.containsKey(relay)) {
            throw new IllegalArgumentException("Relay " + relay + "is not supported.");
        }
        return relayRuntimes.get(relay);
    }
    
    public Map<Integer, Instant> getRelayRuntimeMap() {
        return Maps.newHashMap(relayRuntimes);
    }
}

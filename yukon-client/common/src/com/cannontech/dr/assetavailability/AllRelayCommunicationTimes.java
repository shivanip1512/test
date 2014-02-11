package com.cannontech.dr.assetavailability;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableMap;

public final class AllRelayCommunicationTimes extends DeviceCommunicationTimes {
    private final ImmutableMap<Integer, Instant> relayRuntimes;
    
    public AllRelayCommunicationTimes(Instant lastCommunicationTime, Instant lastNonZeroRuntime, 
            Instant relay1Runtime, Instant relay2Runtime, Instant relay3Runtime, Instant relay4Runtime) {
        super(lastCommunicationTime, lastNonZeroRuntime);
        ImmutableMap.Builder<Integer, Instant> builder = ImmutableMap.builder();
        builder.put(1, relay1Runtime);
        builder.put(2, relay2Runtime);
        builder.put(3, relay3Runtime);
        builder.put(4, relay4Runtime);
        relayRuntimes = builder.build();
    }
    
    public Instant getRelayRuntime(int relay) {
        if(!relayRuntimes.containsKey(relay)) {
            throw new IllegalArgumentException("Relay " + relay + "is not supported.");
        }
        return relayRuntimes.get(relay);
    }
    
    public ImmutableMap<Integer, Instant> getRelayRuntimeMap() {
        return relayRuntimes;
    }
}

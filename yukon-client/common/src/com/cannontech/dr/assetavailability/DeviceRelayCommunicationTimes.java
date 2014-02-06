package com.cannontech.dr.assetavailability;

import org.joda.time.Instant;

public final class DeviceRelayCommunicationTimes extends DeviceCommunicationTimes {
    private final int relay;
    private final Instant relayLastNonZeroRuntime;
    
    public DeviceRelayCommunicationTimes(Instant lastCommunicationTime, Instant lastNonZeroRuntime, int relay, 
                                         Instant relayLastNonZeroRuntime) {
        super(lastCommunicationTime, lastNonZeroRuntime);
        this.relay = relay;
        this.relayLastNonZeroRuntime = relayLastNonZeroRuntime;
    }

    public int getRelay() {
        return relay;
    }

    public Instant getRelayLastNonZeroRuntime() {
        return relayLastNonZeroRuntime;
    }
}

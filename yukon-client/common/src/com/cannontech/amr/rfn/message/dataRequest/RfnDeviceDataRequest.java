package com.cannontech.amr.rfn.message.dataRequest;

import java.io.Serializable;

import org.joda.time.Instant;

/**
 * Sent from WS to SM to initiate DynamicRfnDeviceData update.
 */
public class RfnDeviceDataRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instant lastUpdateTime;

    public RfnDeviceDataRequest(Instant lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Instant getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public String toString() {
        return String.format("RfnDeviceDataRequest [lastUpdateTime=%s]", lastUpdateTime);
    }
}

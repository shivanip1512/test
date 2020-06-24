package com.cannontech.services.infrastructure.service.impl;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;

/**
 * Contains all the relevant info for a gateway connection status check.
 */
public final class ConnectionStatusInfo {
    private PaoIdentifier gateway;
    private Duration warnableDuration;
    private PointValueQualityHolder lastConnectedPointValue;
    private Instant lastConnectedTimestamp;
    private boolean isLastConnectedTimestampWarnable;
    private Instant nextDisconnectedTimestamp;
    private boolean isNextDisconnectedTimestampWarnable;
    
    public ConnectionStatusInfo(PaoIdentifier gateway, Duration warnableDuration,
                                PointValueQualityHolder lastConnectedPointValue) {
        this.gateway = gateway;
        this.warnableDuration = warnableDuration;
        this.lastConnectedPointValue = lastConnectedPointValue;
        // If the gateway has never shown CONNECTED, don't warn.
        if (lastConnectedPointValue != null) {
            lastConnectedTimestamp = new Instant(lastConnectedPointValue.getPointDataTimeStamp());
            // Only warn if the last CONNECTED state occurred more than "warnableDuration" minutes ago.
            if (lastConnectedTimestamp.plus(warnableDuration).isBeforeNow()) {
                isLastConnectedTimestampWarnable = true;
            }
        }
    }
    
    public void setNextDisconnectedPointValue(PointValueHolder nextDisconnectedPointValue) {
        if (nextDisconnectedPointValue != null) {
            nextDisconnectedTimestamp = new Instant(nextDisconnectedPointValue.getPointDataTimeStamp());
            // Only warn if the DISCONNECTED state occurred more than "warnableDuration" minutes ago.
            if (nextDisconnectedTimestamp.plus(warnableDuration).isBeforeNow()) {
                isNextDisconnectedTimestampWarnable = true;
            }
        }
    }
    
    public Instant getNextDisconnectedTimestamp() {
        return nextDisconnectedTimestamp;
    }
    
    public PointValueQualityHolder getLastConnectedPointValue() {
        return lastConnectedPointValue;
    }
    
    public PaoIdentifier getGatewayPaoId() {
        return gateway;
    }
    
    public boolean isLastConnectedTimestampWarnable() {
        return isLastConnectedTimestampWarnable;
    }
    
    public boolean isWarnable() {
        return isLastConnectedTimestampWarnable &&
               isNextDisconnectedTimestampWarnable;
    }
}
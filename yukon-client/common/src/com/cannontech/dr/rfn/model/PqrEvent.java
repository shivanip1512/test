package com.cannontech.dr.rfn.model;

import org.joda.time.Instant;

/**
 * Data object for individual Power Quality Response event.
 */
public final class PqrEvent {
    private final int inventoryId;
    private final Instant timestamp;
    private final PqrEventType eventType;
    private final PqrResponseType responseType;
    private final double value;
    
    public PqrEvent(int inventoryId, Instant timestamp, PqrEventType eventType, PqrResponseType responseType, 
                    double value) {
        this.inventoryId = inventoryId;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.responseType = responseType;
        this.value = value;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public PqrEventType getEventType() {
        return eventType;
    }

    public PqrResponseType getResponseType() {
        return responseType;
    }

    public Double getValue() {
        return value;
    }
}

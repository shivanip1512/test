package com.cannontech.dr.rfn.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

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
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("inventoryId", inventoryId)
                .append("timestamp", ISODateTimeFormat.dateTime().print(timestamp))
                .append("eventType", eventType)
                .append("responseType", responseType)
                .append("value", value)
                .build();
    }
}

package com.cannontech.dr.rfn.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Data object for individual Power Quality Response event.
 */
public final class PqrEvent {
    private final int inventoryId;
    private final RfnIdentifier rfnIdentifier;
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
        
        rfnIdentifier = null;
    }
    
    public PqrEvent(int inventoryId, RfnIdentifier rfnIdentifier, Instant timestamp, PqrEventType eventType, 
                    PqrResponseType responseType, double value) {
        this.inventoryId = inventoryId;
        this.rfnIdentifier = rfnIdentifier;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.responseType = responseType;
        this.value = value;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
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

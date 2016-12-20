package com.cannontech.dr.honeywellWifi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Honeywell DR Event class will hold the honeywell device events information.
 * Currently only few these fields seem important, in future we can add more fields to this class as per requirement.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoneywellDREvent {

    private final Integer eventId;
    private final boolean optOutable;
    private final boolean optedOut;

    @JsonCreator
    public HoneywellDREvent(@JsonProperty("eventID") Integer eventId, 
            @JsonProperty("optOutable") boolean optOutable,
            @JsonProperty("optedOut") boolean optedOut) {
        this.eventId = eventId;
        this.optOutable = optOutable;
        this.optedOut = optedOut;
    }

    public Integer getEventId() {
        return eventId;
    }

    public boolean isOptOutable() {
        return optOutable;
    }

    public boolean isOptedOut() {
        return optedOut;
    }
}

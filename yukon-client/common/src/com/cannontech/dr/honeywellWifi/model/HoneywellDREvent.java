package com.cannontech.dr.honeywellWifi.model;

/**
 * DREvent
 */
public class HoneywellDREvent {

    private final Integer eventId;
    private final boolean optOutable;
    private final boolean optedOut;

    public HoneywellDREvent(Integer eventId, boolean optOutable, boolean optedOut) {
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

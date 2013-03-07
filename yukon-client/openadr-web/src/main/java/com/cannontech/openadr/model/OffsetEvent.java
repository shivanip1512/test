package com.cannontech.openadr.model;

/**
 * This class is used to store information about an OadrEvent from the database
 * for various comparison and unmarshalling purposes.
 */
public class OffsetEvent {
    private final String eventXml;
    private final int offsetMillis;
    
    public OffsetEvent(String eventXml, int offsetMillis) {
        this.eventXml = eventXml;
        this.offsetMillis = offsetMillis;
    }
    
    public String getEventXml() {
        return eventXml;
    }
    
    public int getOffsetMillis() {
        return offsetMillis;
    }
}

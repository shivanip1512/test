package com.cannontech.openadr.exception;

import com.cannontech.openadr.OadrResponseCode;

public class OpenAdrEventException extends Exception {
    
    private final String eventId;
    private final OadrResponseCode code;
    private final long modNumber;
    private final String requestId;
    
    public OpenAdrEventException(String eventId, OadrResponseCode code, long modNumber, String requestId) {
        this.eventId = eventId;
        this.code = code;
        this.modNumber = modNumber;
        this.requestId = requestId;
    }
    
    public OadrResponseCode getCode() {
        return code;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public long getModNumber() {
        return modNumber;
    }
    
    public String getRequestId() {
        return requestId;
    }
}

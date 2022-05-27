package com.cannontech.web.api.der.edge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeBroadcastRequest {
    public enum Priority {
        NON_REAL_TIME,  // Lower priority that may be delayed when the network is congested.
        IMMEDIATE; // High priority for immediate delivery.
    }

    private String payload;
    private Priority priority;

    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

}

package com.cannontech.web.api.der.edge;

import com.cannontech.dr.edgeDr.EdgeBroadcastMessagePriority;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeBroadcastRequest {
    private String payload;
    private EdgeBroadcastMessagePriority priority = EdgeBroadcastMessagePriority.LOW;

    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public EdgeBroadcastMessagePriority getPriority() {
        return this.priority;
    }

    public void setPriority(EdgeBroadcastMessagePriority priority) {
        this.priority = priority;
    }

}

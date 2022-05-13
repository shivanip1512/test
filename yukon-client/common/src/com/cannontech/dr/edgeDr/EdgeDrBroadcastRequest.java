package com.cannontech.dr.edgeDr;

public class EdgeDrBroadcastRequest {
    private final String messageGuid;
    private final byte[] payload;
    private final EdgeBroadcastMessagePriority priority;
    
    public EdgeDrBroadcastRequest(String messageGuid, byte[] payload) {
        this.messageGuid = messageGuid;
        this.payload = payload;
        priority = EdgeBroadcastMessagePriority.NON_REAL_TIME;
    }
    
    public EdgeDrBroadcastRequest(String messageGuid, byte[] payload, EdgeBroadcastMessagePriority priority) {
        this.messageGuid = messageGuid;
        this.payload = payload;
        this.priority = priority;
    }

    public String getMessageGuid() {
        return messageGuid;
    }

    public byte[] getPayload() {
        return payload;
    }
    
    public EdgeBroadcastMessagePriority getPriority() {
        return priority;
    }
}

package com.cannontech.dr.edgeDr;

public class EdgeDrBroadcastRequest {
    private final String messageGuid;
    private final byte[] payload;
    
    public EdgeDrBroadcastRequest(String messageGuid, byte[] payload) {
        this.messageGuid = messageGuid;
        this.payload = payload;
    }

    public String getMessageGuid() {
        return messageGuid;
    }

    public byte[] getPayload() {
        return payload;
    }
}

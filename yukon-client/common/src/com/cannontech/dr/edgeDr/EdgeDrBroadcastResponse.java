package com.cannontech.dr.edgeDr;

public class EdgeDrBroadcastResponse {
    private final String messageGuid;
    private final EdgeDrError error;
    
    public EdgeDrBroadcastResponse(String messageGuid, EdgeDrError error) {
        this.messageGuid = messageGuid;
        this.error = error;
    }
    
    public String getMessageGuid() {
        return messageGuid;
    }

    public EdgeDrError getError() {
        return error;
    }
}

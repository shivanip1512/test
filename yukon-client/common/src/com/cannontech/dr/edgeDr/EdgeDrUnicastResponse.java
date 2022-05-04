package com.cannontech.dr.edgeDr;

import java.util.Map;

public class EdgeDrUnicastResponse {
    private final String messageGuid;
    private final Map<Integer, Short> paoToE2eId;
    private final EdgeDrError error;
    
    public EdgeDrUnicastResponse(String messageGuid, Map<Integer, Short> paoToE2eId, EdgeDrError error) {
        this.messageGuid = messageGuid;
        this.paoToE2eId = paoToE2eId;
        this.error = error;
    }

    public String getMessageGuid() {
        return messageGuid;
    }

    public Map<Integer, Short> getPaoToE2eId() {
        return paoToE2eId;
    }

    public EdgeDrError getError() {
        return error;
    }
}

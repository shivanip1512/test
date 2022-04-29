package com.cannontech.dr.edgeDr;

public class EdgeDrDataNotification {
    private final int paoId;
    private final byte[] payload;
    private final String e2eId;
    private final EdgeDrError error;
    
    public EdgeDrDataNotification(int paoId, byte[] payload, String e2eId, EdgeDrError error) {
        this.paoId = paoId;
        this.payload = payload;
        this.e2eId = e2eId;
        this.error = error;
    }

    public int getPaoId() {
        return paoId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public String getE2eId() {
        return e2eId;
    }

    public EdgeDrError getError() {
        return error;
    }
}

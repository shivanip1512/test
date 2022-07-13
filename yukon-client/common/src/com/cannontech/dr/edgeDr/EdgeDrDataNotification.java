package com.cannontech.dr.edgeDr;

import java.util.Arrays;

public class EdgeDrDataNotification {
    private int paoId;
    private byte[] payload;
    private Short e2eId;
    private EdgeDrError error;
    
    public EdgeDrDataNotification(int paoId, byte[] payload, Short e2eId, EdgeDrError error) {
        this.paoId = paoId;
        this.payload = payload;
        this.e2eId = e2eId;
        this.error = error;
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public Short getE2eId() {
        return e2eId;
    }

    public void setE2eId(Short e2eId) {
        this.e2eId = e2eId;
    }

    public EdgeDrError getError() {
        return error;
    }

    public void setError(EdgeDrError error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "EdgeDrDataNotification [paoId=" + paoId + ", payload=" + Arrays.toString(payload) + ", e2eId=" + e2eId
                + ", error=" + error + "]";
    }
}

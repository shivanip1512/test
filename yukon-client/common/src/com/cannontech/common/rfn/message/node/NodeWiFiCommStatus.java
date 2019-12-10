package com.cannontech.common.rfn.message.node;

import java.util.Arrays;

public enum NodeWiFiCommStatus {
    ACTIVE(0),
    NOT_ACTIVE(1),
    ;
    
    private final short nodeWiFiCommStatusCodeID;

    private NodeWiFiCommStatus(int nodeWiFiCommStatusCodeID) {
        this.nodeWiFiCommStatusCodeID = (short) nodeWiFiCommStatusCodeID;
    }

    public short getNodeWiFiCommStatusCodeID() {
        return nodeWiFiCommStatusCodeID;
    }
    
    /**
     * @return the Enum representation for the given nodeCommStatusCodeID.
     *         null if unknown nodeCommStatusCodeID.
     */
    public static NodeWiFiCommStatus fromNodeWiFiCommStatusCodeID(short nodeCommStatusCodeID) {
        return Arrays.stream(NodeWiFiCommStatus.values())
            .filter(v -> v.getNodeWiFiCommStatusCodeID() == nodeCommStatusCodeID)
            .findFirst()
            .orElse(null);
    }
}
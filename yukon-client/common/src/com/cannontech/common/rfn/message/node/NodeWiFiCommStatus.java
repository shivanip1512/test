package com.cannontech.common.rfn.message.node;

import java.util.Arrays;

public enum NodeWiFiCommStatus {
    ACTIVE((short)0x0000),
    NOT_ACTIVE((short)0x0001),
    ;
    
    private final short nodeWiFiCommStatusCodeID;

    private NodeWiFiCommStatus(short nodeWiFiCommStatusCodeID) {
        this.nodeWiFiCommStatusCodeID = nodeWiFiCommStatusCodeID;
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
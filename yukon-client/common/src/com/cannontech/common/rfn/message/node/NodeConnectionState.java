package com.cannontech.common.rfn.message.node;

import java.util.Arrays;

public enum NodeConnectionState {
    NOT_ACTIVE(1),
    ACTIVE(2),
    ;

    private final short nodeWiFiCommStatusCodeID;

    private NodeConnectionState(int nodeWiFiCommStatusCodeID) {
        this.nodeWiFiCommStatusCodeID = (short) nodeWiFiCommStatusCodeID;
    }

    public short getNodeWiFiCommStatusCodeID() {
        return nodeWiFiCommStatusCodeID;
    }

    /**
     * @return the Enum representation for the given nodeCommStatusCodeID.
     *         null if unknown nodeCommStatusCodeID.
     */
    public static NodeConnectionState fromNodeWiFiCommStatusCodeID(short nodeCommStatusCodeID) {
        return Arrays.stream(NodeConnectionState.values())
                .filter(v -> v.getNodeWiFiCommStatusCodeID() == nodeCommStatusCodeID)
                .findFirst()
                .orElse(null);
    }
}
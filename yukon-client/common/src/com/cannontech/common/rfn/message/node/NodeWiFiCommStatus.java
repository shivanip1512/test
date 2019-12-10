package com.cannontech.common.rfn.message.node;

import java.util.Arrays;

public enum NodeWiFiCommStatus {
    ACTIVE(2),
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
     * @return the COMM_STATUS state corresponding to the nodeWiFiCommStatusCodeID
     * 
     * NM is sending the code as ACTIVE (2), NOT_ACTIVE (1).
     * The Stategroup COMM_STATUS (-11) needs the values as Connected (0), Disconnected (1)
     * 2 % 2, ACTIVE (2) --> Connected (0)
     * 1 % 2, NOT_ACTIVE --> Disconnected (1)
     */
    public short getNodeWiFiCommStatusStateGroupState() {
        return (short) (nodeWiFiCommStatusCodeID % 2);
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
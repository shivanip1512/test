package com.cannontech.common.rfn.message.node;

import java.util.Arrays;

public enum NodeCommStatus {
    READY((short)0x0001),
    NOT_READY((short)0x0002),
    ;
    
    private final short nodeCommStatusCodeID;

    private NodeCommStatus(short nodeCommStatusCodeID) {
        this.nodeCommStatusCodeID = nodeCommStatusCodeID;
    }

    public short getNodeCommStatusCodeID() {
        return nodeCommStatusCodeID;
    }
    
    /**
     * @return the Enum representation for the given nodeCommStatusCodeID.
     *         null if unknown nodeCommStatusCodeID.
     */
    public static NodeCommStatus fromNodeCommStatusCodeID(short nodeCommStatusCodeID) {
        return Arrays.stream(NodeCommStatus.values())
            .filter(v -> v.getNodeCommStatusCodeID() == nodeCommStatusCodeID)
            .findFirst()
            .orElse(null);
    }
}
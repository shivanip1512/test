package com.cannontech.common.rfn.message.metadatamulti;

import java.util.Arrays;

public enum NodeType {
    GATEWAY(0),
    ELECTRIC_NODE(1),
    GAS_NODE(2),
    WATER_NODE(3),
    LCR_NODE(4),
    DA_NODE(5),
    EKANET_RELAY(127),
    ;
    
    private final int nodeTypeCode;

    private NodeType(int nodeTypeCode) {
        this.nodeTypeCode = nodeTypeCode;
    }

    public int getNodeTypeCode() {
        return nodeTypeCode;
    }
    
    /**
     * @return the Enum representation for the given nodeTypeCode.
     *         null if unknown nodeTypeCode.
     */
    public static NodeType fromNodeTypeCode(int nodeTypeCode) {
        return Arrays.stream(NodeType.values())
            .filter(v -> v.getNodeTypeCode() == nodeTypeCode)
            .findFirst()
            .orElse(null);
    }
}
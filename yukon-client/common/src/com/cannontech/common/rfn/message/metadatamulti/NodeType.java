package com.cannontech.common.rfn.message.metadatamulti;

public enum NodeType {
    
    GATEWAY("Gateway"),
    
    ELECTRIC_NODE("Electric node"),
    
    GAS_NODE("Gas node"),
    
    WATER_NODE("Water node"),
    
    LCR_NODE("LCR node"),
    
    DA_NODE("DA node"),
    
    EKANET_RELAY("EkaNet relay"),
    
    UNKNOWN("Unknown node type"),
    
    ;
    
    private final String nodeTypeString;
    
    private NodeType(String nodeTypeString) {
        this.nodeTypeString = nodeTypeString;
    }

    public String getNodeTypeString() {
        return nodeTypeString;
    }
}
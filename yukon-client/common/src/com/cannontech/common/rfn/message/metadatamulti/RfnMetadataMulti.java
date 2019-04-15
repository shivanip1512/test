package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;

// Each constant can be query independently
public enum RfnMetadataMulti implements Serializable {
    
    // null indicates the primary gateway is unknown. 
    PRIMARY_GATEWAY_COMM(PrimaryGatewayComm.class),
    
    NODE_DATA(NodeData.class),
    
    // This replaces RfnMetadata.NUM_ASSOCIATIONS
    // null indicates the meter doesn't support battery node association.
    READY_BATTERY_NODE_COUNT(Integer.class),
    ;
   
    private final Class<?> constantClass;
    
    private RfnMetadataMulti(Class<?> constantClass) {
        this.constantClass = constantClass;
    }
    
    public Class<?> getConstantClass() {
        return constantClass;
    }
}
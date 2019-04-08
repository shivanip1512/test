package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;

// Each constant can be query independently
public enum RfnMetadataMulti implements Serializable {
    
    ALL(Object.class),  // Selecting All alone indicates you are interested in getting all metadata
                        // Any other constant selected with ALL is treated as exclusive
                        // In other words, we support "ALL but ..." 
    
    PRIMARY_GATEWAY_COMM(PrimaryGatewayComm.class),
    
    NODE_DATA(NodeData.class),
    
    READY_BATTERY_NODE_COUNT(Integer.class), // Not support yet;
                                             // will replace RfnMetadata.NUM_ASSOCIATIONS
    ;
   
    private final Class<?> constantClass;
    
    private RfnMetadataMulti(Class<?> constantClass) {
        this.constantClass = constantClass;
    }
    
    public Class<?> getConstantClass() {
        return constantClass;
    }
}
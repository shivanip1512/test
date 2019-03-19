package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;

// Each constant maps to just one query.
public enum RfnMetadataMulti implements Serializable {
    
    ALL(Object.class), // indicates you are interested in getting all metadata
    
    PRIMARY_GATEWAY_COMM(PrimaryGatewayComm.class),
    
    NODE_DATA(NodeData.class),
    ;
   
    private final Class<?> constantClass;
    
    private RfnMetadataMulti(Class<?> constantClass) {
        this.constantClass = constantClass;
    }
    
    public Class<?> getConstantClass() {
        return constantClass;
    }
}
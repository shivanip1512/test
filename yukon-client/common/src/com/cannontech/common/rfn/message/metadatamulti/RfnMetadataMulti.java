package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Each constant can be query independently
public enum RfnMetadataMulti implements Serializable {
    
    // null indicates the primary gateway is unknown. 
    PRIMARY_GATEWAY_COMM(PrimaryGatewayComm.class, EntityType.NODE),
    
    NODE_DATA(NodeData.class, EntityType.NODE),
    
    // This replaces RfnMetadata.NUM_ASSOCIATIONS
    // null indicates the meter doesn't support battery node association.
    READY_BATTERY_NODE_COUNT(Integer.class, EntityType.NODE),
    
    GATEWAY_NODES(GatewayNodes.class, EntityType.GATEWAY),

    ;
   
    private final Class<?> constantClass;
    
    private final Set<EntityType> entityTypes;
    
    private RfnMetadataMulti(Class<?> constantClass, EntityType... entityTypes) {
        this.constantClass = constantClass;
        this.entityTypes = new HashSet<>(Arrays.asList(entityTypes));
    }
    
    public Class<?> getConstantClass() {
        return constantClass;
    }
    
    public Set<EntityType> getEntityTypes() {
        return entityTypes;
    }
}
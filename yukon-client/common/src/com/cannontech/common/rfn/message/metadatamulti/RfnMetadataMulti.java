package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.metadatamulti.neighbor.Neighbor;
import com.cannontech.common.rfn.message.metadatamulti.neighbor.Neighborhood;

// Each constant can be query independently
public enum RfnMetadataMulti implements Serializable {

    // ------ First Part: replace metadata package ------
    // This covers RfnMetadata.PRIMARY_GATEWAY,
    //             RfnMetadata.COMM_STATUS
    //             RfnMetadata.COMM_STATUS_TIMESTAMP
    // null indicates the primary gateway is unknown.
    PRIMARY_GATEWAY_NODE_COMM(NodeComm.class, EntityType.NODE),
    
    // Number of Hops to Primary Gateway:
    // This replaces RfnMetadata.PRIMARY_GATEWAY_HOP_COUNT
    PRIMARY_GATEWAY_HOP_COUNT(Integer.class, EntityType.NODE),
    
    // This covers RfnMetadata.NODE_SERIAL_NUMBER
    //             RfnMetadata.HARDWARE_VERSION
    //             RfnMetadata.IN_NETWORK_TIMESTAMP
    //             RfnMetadata.NODE_ADDRESS
    //             RfnMetadata.NODE_FIRMWARE_VERSION
    //             RfnMetadata.NODE_TYPE
    NODE_DATA(NodeData.class, EntityType.NODE),

    // This replaces RfnMetadata.NUM_ASSOCIATIONS
    // null indicates the meter doesn't support battery node association.
    READY_BATTERY_NODE_COUNT(Integer.class, EntityType.NODE),
    
    // Current Number of Neighbors:
    // This replaces RfnMetadata.NEIGHBOR_COUNT
    NEIGHBOR_COUNT(Integer.class, EntityType.GATEWAY, EntityType.NODE),

    // Current Primary Forward Neighbor:
    // This replaces RfnMetadata.PRIMARY_NEIGHBOR,
    //               RfnMetadata.PRIMARY_NEIGHBOR_DATA_TIMESTAMP
    //               RfnMetadata.PRIMARY_NEIGHBOR_LINK_COST
    PRIMARY_FORWARD_NEIGHBOR(Neighbor.class, EntityType.NODE),

    // ------ Second part: replace network package ------
    // This replaces RfnNeighborDataRequest
    NEIGHBORS(Neighborhood.class, EntityType.GATEWAY, EntityType.NODE),

    // This replaces RfnPrimaryRouteDataRequest
    // PRIMARY_FORWARD_ROUTE(Route.class, EntityType.NODE),
    
    // ------ Third part: new features ------
    PRIMARY_GATEWAY_NODES(GatewayNodes.class, EntityType.GATEWAY),
    
    // PRIMARY_FORWARD_DESCENDENTS(Descendent.class, EntityType.GATEWAY, EntityType.NODE),
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
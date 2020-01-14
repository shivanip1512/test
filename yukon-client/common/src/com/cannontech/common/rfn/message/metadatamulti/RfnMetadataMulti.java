package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.message.route.RfnRoute;
import com.cannontech.common.rfn.message.route.RouteData;
import com.cannontech.common.rfn.message.tree.RfnVertex;

/**
 * Query (constant) can be used alone or combined.
 * The first parameter of a constant indicates its response type.
 * Please look at each type so you know which query you are going to use.
 * Second and third parameters are not supported yet so you can ignore them. 
 * 
 * @author lizhu2
 *
 */
public enum RfnMetadataMulti implements Serializable {

    // Replace RfnMetadata.PRIMARY_GATEWAY,
    //         RfnMetadata.COMM_STATUS
    //         RfnMetadata.COMM_STATUS_TIMESTAMP
    // Note: null indicates the primary gateway is unknown.
    PRIMARY_GATEWAY_NODE_COMM(NodeComm.class, 1000, EntityType.NODE), // ready for integration test
    
    // Replace RfnMetadata.NODE_SERIAL_NUMBER
    //         RfnMetadata.HARDWARE_VERSION
    //         RfnMetadata.IN_NETWORK_TIMESTAMP
    //         RfnMetadata.NODE_ADDRESS
    //         RfnMetadata.NODE_FIRMWARE_VERSION
    //         RfnMetadata.NODE_TYPE
    NODE_DATA(NodeData.class, 1000, EntityType.NODE), // ready for integration test

    // Replace RfnMetadata.NUM_ASSOCIATIONS
    // Note: null indicates the meter doesn't support battery node association.
    READY_BATTERY_NODE_COUNT(Integer.class, 1000, EntityType.NODE), // ready for integration test
    
    // Replace RfnMetadata.NEIGHBOR_COUNT
    NEIGHBOR_COUNT(Integer.class, 1000, EntityType.GATEWAY, EntityType.NODE),

    // Replace RfnMetadata.PRIMARY_NEIGHBOR,
    //         RfnMetadata.PRIMARY_NEIGHBOR_DATA_TIMESTAMP
    //         RfnMetadata.PRIMARY_NEIGHBOR_LINK_COST
    PRIMARY_FORWARD_NEIGHBOR_DATA(NeighborData.class, 1000, EntityType.NODE), // ready for integration test
    
    // Replace RfnMetadata.PRIMARY_GATEWAY_HOP_COUNT
    // RouteData provides totalCost and hopCount to calculate average link quality
    // (YUK-19751, YUK-20063, YUK-20064)
    PRIMARY_FORWARD_ROUTE_DATA(RouteData.class, 1000, EntityType.NODE), 
    
    // Support network tree query: descendant count (YUK-20089, YUK-20090) and primary route to Gateway
    PRIMARY_FORWARD_DESCENDANT_COUNT(Integer.class, 1000, EntityType.NODE),

    PRIMARY_FORWARD_TREE(RfnVertex.class, 1000, EntityType.NODE),
    
    PRIMARY_FORWARD_ROUTE(RfnRoute.class, 1000, EntityType.NODE),
    
    // PRIMARY_FORWARD_CHILDREN(RfnChildren.class, 1000, EntityType.NODE), // reserved for future use
    
    PRIMARY_FORWARD_GATEWAY(RfnIdentifier.class, 1000, EntityType.NODE),
        
    // The following query will replace the legacy network.RfnNeighborDataRequest eventually
    // NEIGHBOR(Neighbor.class, 1000, EntityType.GATEWAY, EntityType.NODE),

    // Note: the following query uses the legacy NM-decided primary gateway,
    // Since it may be different from the primary forward route gateway, it should not be used for any RF-Mapping query.
    // Once Yukon replaces it with PRIMARY_FORWARD_GATEWAY, PRIMARY_FORWARD_ROUTE and PRIMARY_FORWARD_TREE,
    // I am going to remove this value.
    PRIMARY_GATEWAY_NODES(GatewayNodes.class, 1000, EntityType.GATEWAY),
    ;
   
    private final Class<?> constantClass;
    
    private final int maxEntity;
    
    private final Set<EntityType> entityTypes;
    
    private RfnMetadataMulti(Class<?> constantClass, int maxEntity, EntityType... entityTypes) {
        this.constantClass = constantClass;
        if (maxEntity < 1) {
            this.maxEntity = 1;
        } else {
            this.maxEntity = maxEntity;
        }
        this.entityTypes = new HashSet<>(Arrays.asList(entityTypes));
    }
    
    public Class<?> getConstantClass() {
        return constantClass;
    }
    
    public Set<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public int getMaxEntity() {
        return maxEntity;
    }
    
    public static int getMaxEntity(Set<RfnMetadataMulti> requests) {
        return requests.stream()
                .min(Comparator.comparing(RfnMetadataMulti::getMaxEntity))
                .get()
                .getMaxEntity();
    }
}
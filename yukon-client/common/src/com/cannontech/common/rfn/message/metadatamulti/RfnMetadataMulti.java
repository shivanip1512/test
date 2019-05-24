package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.message.neighbor.Neighbor;
import com.cannontech.common.rfn.message.neighbor.NeighborData;

/**
 * Each constant can be used alone or combined.
 * Before you use a query, make sure you understand the constructor arguments
 *     specified in each constant. 
 * 
 * 1. Make sure the EntityType of your rfnIdentifiers is correct.
 * For example, you can query PRIMARY_GATEWAY_NODE_COMM for node/device but not for gateway.
 * On the other hand, you can query PRIMARY_GATEWAY_NODES for gateway but not for node/device. 
 * Basically you are not allowed to add gatewayRfnIdentifier and deviceRfnIdentifier
 *     in one request. You need to first specify the entityType when creating a request.
 * For example, you add PRIMARY_GATEWAY_NODE_COMM and PRIMARY_GATEWAY_NODES in one request
 * and also add gatewayRfnIdentifiers and deviceRfnIdentifiers into the request.
 * If your request's entityType is NODE, PRIMARY_GATEWAY_NODES will be ignored
 *     and all rfnIdentifiers will be treated as node/device.
 * If your request's entityType is Gateway, PRIMARY_GATEWAY_NODE_COMM will be ignored
 *     and all rfnIdentifiers will be treated as gateway.
 * Based on the above rule, you will find although NEIGHBOR can be queried
 *     for both node/device and gateway, you still need to separate your them into two requests,
 *     one EntityType is NODE and one EntityType is GATEWAY.
 * 
 * 2. Make sure the max rfnIdentifiers you can query.
 * For example, you can query PRIMARY_GATEWAY_NODE_COMM for up to 1000 devices
 * If you query multiple constants, the minimum of their maxEntity(s) will be applied.
 * If the number of rfnIdentifiers in your request is beyond the limit,
 *     you will receive YUKON_INPUT_ERROR with error info in the responseMessage field.
 * 
 * @author lizhu2
 *
 */
public enum RfnMetadataMulti implements Serializable {

    // ------ First Part: replace metadata package ------
    // This covers RfnMetadata.PRIMARY_GATEWAY,
    //             RfnMetadata.COMM_STATUS
    //             RfnMetadata.COMM_STATUS_TIMESTAMP
    // null indicates the primary gateway is unknown.
    PRIMARY_GATEWAY_NODE_COMM(NodeComm.class, 1000, EntityType.NODE),
    
    // Number of Hops to Primary Gateway:
    // This replaces RfnMetadata.PRIMARY_GATEWAY_HOP_COUNT
    PRIMARY_GATEWAY_HOP_COUNT(Integer.class, 1000, EntityType.NODE),
    
    // This covers RfnMetadata.NODE_SERIAL_NUMBER
    //             RfnMetadata.HARDWARE_VERSION
    //             RfnMetadata.IN_NETWORK_TIMESTAMP
    //             RfnMetadata.NODE_ADDRESS
    //             RfnMetadata.NODE_FIRMWARE_VERSION
    //             RfnMetadata.NODE_TYPE
    NODE_DATA(NodeData.class, 1, EntityType.NODE),

    // This replaces RfnMetadata.NUM_ASSOCIATIONS
    // null indicates the meter doesn't support battery node association.
    READY_BATTERY_NODE_COUNT(Integer.class, 1000, EntityType.NODE),
    
    // Current Number of Neighbors:
    // This replaces RfnMetadata.NEIGHBOR_COUNT
    NEIGHBOR_COUNT(Integer.class, 1000, EntityType.GATEWAY, EntityType.NODE),

    // Current Primary Forward Neighbor:
    // This replaces RfnMetadata.PRIMARY_NEIGHBOR,
    //               RfnMetadata.PRIMARY_NEIGHBOR_DATA_TIMESTAMP
    //               RfnMetadata.PRIMARY_NEIGHBOR_LINK_COST
    PRIMARY_FORWARD_NEIGHBOR_DATA(NeighborData.class, 1000, EntityType.NODE),

    // ------ Second part: replace network package ------
    // This replaces RfnNeighborDataRequest
    NEIGHBOR(Neighbor.class, 1000, EntityType.GATEWAY, EntityType.NODE),

    // ROUTES, PRIMARY_FORWARD_ROUTES, PRIMARY_FORWARD_ROUTES_BY_NEXT_HOP,
    // PRIMARY_FORWARD_ROUTES_BY_DESTINATION_AND_NEXT_HOP, etc., could also be provided.
    // So far only PRIMARY_FORWARD_ROUTES_TO_GATEWAY is needed, which returns
    // all PRIMARY_FORWARD_ROUTES from this node to a gateway.
    // The destination must be a gateway and unique in the return station.
    // It replaces RfnPrimaryRouteDataRequest.
    //PRIMARY_FORWARD_ROUTES_TO_GATEWAY(Route.class, 1000, EntityType.NODE),
    
    // ------ Third part: new features ------
    
    // GATEWAY_NODES(GatewayNodes.class, EntityType.GATEWAY), // not used now
    
    // PRIMARY_GATEWAY_NODES use the gateway as their primary gateway while
    //         GATEWAY_NODES above may not. GATEWAY_NODES are all nodes on the gateway's node list.
    // A node can associate with multiple gateways but
    // a primary node can only belong to one gateway or its primary gateway.   
    PRIMARY_GATEWAY_NODES(GatewayNodes.class, 1000, EntityType.GATEWAY),
    
    //PRIMARY_FORWARD_NODES(Route.class, 1000, EntityType.GATEWAY, EntityType.NODE),
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
    
    public static int getChunkSize(Set<RfnMetadataMulti> requests) {
        return requests.stream()
                .min(Comparator.comparing(RfnMetadataMulti::getMaxEntity))
                .get()
                .getMaxEntity();
    }
}
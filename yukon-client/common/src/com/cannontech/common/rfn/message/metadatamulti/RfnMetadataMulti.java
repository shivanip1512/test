package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.neighbor.Neighbors;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.message.node.NodeNetworkInfo;
import com.cannontech.common.rfn.message.route.RfnRoute;
import com.cannontech.common.rfn.message.route.RouteData;
import com.cannontech.common.rfn.message.tree.RfnVertex;

/**
 * Query (constant) can be used alone or combined.
 * The first parameter of a constant indicates its response type.
 * This is important since it defines your query results.
 * Please look at each response type first so you know which query you should use.
 * Second and third parameters are not supported yet so you may just ignore them now. 
 * 
 * @author lizhu2
 */
public enum RfnMetadataMulti implements Serializable {

    // Note: null indicates the reverse lookup gateway is unknown.
    REVERSE_LOOKUP_NODE_COMM(NodeComm.class, 1000, EntityType.NODE),
    
    // Note: can't be null when RfnMetadataMultiQueryResultType is OK.
    NODE_DATA(NodeData.class, 1000, EntityType.NODE),
    
    // Note: can't be null when RfnMetadataMultiQueryResultType is OK.
    NODE_NETWORK_INFO(NodeNetworkInfo.class, 1000, EntityType.NODE),

    // Note: null indicates the meter doesn't support battery node association.
    READY_BATTERY_NODE_COUNT(Integer.class, 1000, EntityType.NODE),
    
    // Note: null indicates the entity haven't sent
    //       its neighbor data recently (default 9 days).
    NEIGHBOR_COUNT(Integer.class, 1000, EntityType.GATEWAY, EntityType.NODE),

    // NeighborData provides neighborLinkCost, numSamples, etxBand, etc.
    // Note: null indicates the entity hasn't sent
    //       its primary neighbor data recently (default 9 days).
    PRIMARY_FORWARD_NEIGHBOR_DATA(NeighborData.class, 1000, EntityType.NODE),
    
    // RouteData provides totalCost, hopCount, etc.
    // Note: null indicates the entity hasn't sent
    //       its primary route data recently (default 9 days).
    PRIMARY_FORWARD_ROUTE_DATA(RouteData.class, 1000, EntityType.NODE),
    
    // Note: null indicates the node does not have primary forward route
    PRIMARY_FORWARD_ROUTE(RfnRoute.class, 1000, EntityType.NODE),
    
    // Note: null indicates the entity hasn't sent
    //       its neighbor data recently (default 9 days).
    NEIGHBORS(Neighbors.class, 1000, EntityType.GATEWAY, EntityType.NODE),
    
    // Note: null indicates the battery node doesn't have a parent.
    BATTERY_NODE_PARENT(RfnIdentifier.class, 1000, EntityType.NODE),
    
    // Note: null indicates the entity is not under any tree.
    PRIMARY_FORWARD_DESCENDANT_COUNT(Integer.class, 1000, EntityType.GATEWAY, EntityType.NODE),
    
    // Note: null indicates the entity is not under any tree.
    PRIMARY_FORWARD_TREE(RfnVertex.class, 1000, EntityType.GATEWAY, EntityType.NODE),

    // Note: null indicates the node does not have primary forward gateway
    PRIMARY_FORWARD_GATEWAY(RfnIdentifier.class, 1000, EntityType.NODE),
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
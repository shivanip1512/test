package com.cannontech.web.tools.mapping.service.impl;

import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_TREE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.tree.RfnVertex;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.rfn.simulation.util.NetworkDebugHelper;
import com.cannontech.common.util.tree.Node;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.service.NetworkTreeService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

public class NetworkTreeServiceImpl implements NetworkTreeService {
    
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnDeviceMetadataMultiService metadataMultiService;
    @Autowired private RfnGatewayService rfnGatewayService;
    private static final Logger log = YukonLogManager.getLogger(NetworkTreeServiceImpl.class);
    
    private final Cache<RfnIdentifier, Node<Pair<Integer, FeatureCollection>>> networkTreeCache =
            CacheBuilder.newBuilder().expireAfterWrite(8, TimeUnit.HOURS).build();
    
    @Override
    public List<Node<Pair<Integer, FeatureCollection>>> getNetworkTree(List<Integer> gatewayIds)
            throws NmNetworkException, NmCommunicationException {
        
        List<Node<Pair<Integer, FeatureCollection>>> trees = new ArrayList<>();
        
        Map<RfnIdentifier, RfnGateway> gatewaysToSendToNM = new HashMap<>();
        
        rfnGatewayService.getGatewaysByPaoIds(gatewayIds).forEach(gateway -> {
            Node<Pair<Integer, FeatureCollection>> tree = networkTreeCache.getIfPresent(gateway.getRfnIdentifier());
            if(tree != null) {
                log.debug("{} found in cache", gateway.getRfnIdentifier());
                trees.add(tree);
            } else {
                gatewaysToSendToNM.put(gateway.getRfnIdentifier(), gateway);
            }
        });

        if(gatewaysToSendToNM.isEmpty() && trees.isEmpty()) {
            log.error("Network tree is per gateway, not gateways {}", gatewayIds);
            return trees;
        }

        if(gatewaysToSendToNM.isEmpty()) {
            //all trees are cached
            return trees;
        }
        
        log.debug("Getting Network trees from NM for gateways: {}", gatewaysToSendToNM.keySet());
        
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = metadataMultiService
                .getMetadataForDeviceRfnIdentifiers(gatewaysToSendToNM.keySet(), Set.of(PRIMARY_FORWARD_TREE));

        Map<Integer, PaoLocation> locations = getLocationsForDevicesAndGateways(gatewaysToSendToNM.values());
        
        for (Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data : metaData.entrySet()) {
            if (data.getValue().isValidResultForMulti(PRIMARY_FORWARD_TREE)) {
                RfnVertex vertex = (RfnVertex) data.getValue().getMetadatas().get(PRIMARY_FORWARD_TREE);
                log.debug("{} Recieved NM VERTEX node count {}", data.getKey(), NetworkDebugHelper.count(vertex));
                RfnGateway gateway = gatewaysToSendToNM.get(data.getKey());
                if(gateway == null) {
                    log.info("Received network tree for gateway {} that we didn't request, ignoring the network tree", gateway);
                    continue;
                }
                trees.add(createTree(vertex, locations));
            }
        }

        return trees;
    }
    
    /**
     * Converts NM tree to Yukon tree and returns Yukon tree. Caches the Yukon tree.
     */
    private Node<Pair<Integer, FeatureCollection>> createTree(RfnVertex vertex,  Map<Integer, PaoLocation> locations) {
        Node<Pair<Integer, FeatureCollection>> node = createNode(vertex.getRfnIdentifier(), locations); 
        AtomicInteger totalNodesAdded = new AtomicInteger(1);
        copy(vertex, node, locations, totalNodesAdded);
        networkTreeCache.put(vertex.getRfnIdentifier(), node);
        log.debug("{} Yukon NODE total node count {} null node count {}  gateway node {}",
                vertex.getRfnIdentifier(), node.count(false), node.count(true), node.getData());
            //log.info(node.print());
        return node;
    }

    /**
     * Returns location for gateways and devices associated with gateways
     */
    private Map<Integer, PaoLocation> getLocationsForDevicesAndGateways(Collection<RfnGateway> gateways) {
        Set<Integer> filteredGatewayIds = gateways.stream().map(gateway -> gateway.getPaoIdentifier()
                .getPaoId()).collect(Collectors.toSet());
        
        //device locations
        List<PaoLocation> allLocations = paoLocationDao.getLocationsByGateway(filteredGatewayIds);
        //add gateway locations
        allLocations.addAll(paoLocationDao.getLocations(filteredGatewayIds));
        
        Map<Integer, PaoLocation> locations = Maps.uniqueIndex(allLocations, location -> location.getPaoIdentifier().getPaoId());
        log.debug("Locations found: {}", locations);
        return locations;
    }
    
    /**
     * Creates Node from rfnIdentifier and location
     */
    private Node<Pair<Integer, FeatureCollection>> createNode(RfnIdentifier rfnIdentifier,  Map<Integer, PaoLocation> locations) {
        if(rfnIdentifier == null) {
            //NN returned null rfnIdentifier
            return new Node<Pair<Integer, FeatureCollection>>(null);
        }
        
        //if device is not found create device
        int deviceId = rfnDeviceCreationService.createIfNotFound(rfnIdentifier).getPaoIdentifier().getPaoId();
        PaoLocation location = locations.get(deviceId);
        //if no location in Yukon database featureCollection will be null
        FeatureCollection featureCollection = location == null ? null : paoLocationService.getFeatureCollection(location);
        return new Node<Pair<Integer, FeatureCollection>>(Pair.of(deviceId, featureCollection));
    }
    
    /**
     * Copies RfnVertex to Node
     */
    private void copy(RfnVertex vertex, Node<Pair<Integer, FeatureCollection>> parent, Map<Integer, PaoLocation> locations,
            AtomicInteger totalNodesAdded) {
        if (vertex.getChildren() == null) {
            return;
        }
        for (Iterator<RfnVertex> it = vertex.getChildren().iterator(); it.hasNext();) {
            RfnVertex nextNode = it.next();
            Node<Pair<Integer, FeatureCollection>> child = createNode(nextNode.getRfnIdentifier(), locations);
            parent.addChild(child);
            totalNodesAdded.incrementAndGet();
            copy(nextNode, child, locations, totalNodesAdded);
        }
    }
}

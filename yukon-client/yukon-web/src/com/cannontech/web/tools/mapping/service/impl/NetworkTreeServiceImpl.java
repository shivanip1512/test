package com.cannontech.web.tools.mapping.service.impl;

import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_DESCENDANT_COUNT;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_GATEWAY;
import static com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti.PRIMARY_FORWARD_TREE;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.geojson.FeatureCollection;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.tree.NetworkTreeUpdateTimeRequest;
import com.cannontech.common.rfn.message.tree.NetworkTreeUpdateTimeResponse;
import com.cannontech.common.rfn.message.tree.RfnVertex;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnDeviceMetadataMultiService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.rfn.simulation.util.NetworkDebugHelper;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.common.util.tree.Node;
import com.cannontech.common.util.tree.Node.TreeDebugStatistics;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.service.NetworkTreeService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

/**
 * This service contains cached (networkTreeCache) Network Trees we receive from NM 
 * 
 * The cache is cleared every day at midnight local time. NM will regenerate trees over night and send a NetworkTreeUpdateTimeResponse when it is done.
 * When this service receives NetworkTreeUpdateTimeResponse it will cache it. NetworkTreeUpdateTimeResponse contains the information UI needs:
 * TreeGenerationEndTimeMillis - is used to display to the user the last tree refresh time.
 * NoForceRefreshBeforeTimeMillis - is used to figure out if Yukon can send a message to NM to do the tree load.
 * 
 * Example:
 * It is 12am the networkTreeCache is cleared we got message from NM later that the Network Trees are regenerated.
 * We cached the message (treeUpdateResponse).
 * At 8am user want to see a Network Tree. Yukon sends a message to NM to get the tree and caches the received tree.
 * At 9am user wants to see 2 trees the one we cached and a new one. Yukon gets one tree from cache a sends a request to NM to get a second tree and caches the tree.
 * At 10am user presses the "reload tree" button. Yukon will send a message to NM to reload trees and disable the button for 30 minutes
 * Yukon receives NetworkTreeUpdateTimeResponse when NM is finished reloading the trees.
 * Yukon will cache NetworkTreeUpdateTimeResponse (treeUpdateResponse) and send NM request to get cached trees (trees in networkTreeCache)
 * Upon receiving the new trees Yukon will reload the cached trees.
 * It is 12am the networkTreeCache is cleared.
 *
 *
 *If WS is restarted during that day we will ask NM for NetworkTreeUpdateTimeResponse and cache when user asks for the tree see getNetworkTree method
 */

public class NetworkTreeServiceImpl implements NetworkTreeService, MessageListener {
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnDeviceMetadataMultiService metadataMultiService;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    protected JmsTemplate jmsTemplate;
    private static final Logger log = YukonLogManager.getLogger(NetworkTreeServiceImpl.class);
    private final DateTimeFormatter df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    
    //gateway/tree
    private final Cache<RfnIdentifier, Node<Pair<Integer, FeatureCollection>>> networkTreeCache = CacheBuilder.newBuilder().build();
    private NetworkTreeUpdateTimeResponse treeUpdateResponse;
    private Instant nextForceReloadTime;
    private int reloadFrequencyInMinutes = 30;
    private int MINUTES_TO_WAIT_TO_ASK_FOR_TREE_TIME_UPDATE = 3;

    
    @PostConstruct
    public void init() {
        scheduledExecutorService.schedule(() -> {
            NetworkTreeUpdateTimeRequest request = new NetworkTreeUpdateTimeRequest();
            log.info("Sending NetworkTreeUpdateTimeRequest message to request network tree information.");
            jmsTemplate.convertAndSend(JmsApiDirectory.NETWORK_TREE_UPDATE_REQUEST.getQueue().getName(), request);
        }, MINUTES_TO_WAIT_TO_ASK_FOR_TREE_TIME_UPDATE, TimeUnit.MINUTES);
    }
     
    //clear cache every day at midnight local time
    {
        //delay
        long millisUntilMidnight = Duration
                .between(LocalDateTime.now(), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT))
                .toMillis();
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> clearCache(), millisUntilMidnight, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }
    
    private void clearCache() {
        long sizeBefore = networkTreeCache.size();
        networkTreeCache.invalidateAll();
        log.debug("Cleared Network Tree Cache, cache size before {} after {}", sizeBefore, networkTreeCache.size());
    }
    
    @Override
    public boolean isNetworkTreeUpdatePossible() {
        return (treeUpdateResponse != null && Instant.now().isAfter(new Instant(treeUpdateResponse.getNoForceRefreshBeforeTimeMillis())))
                &&
               (nextForceReloadTime == null || Instant.now().isAfter(nextForceReloadTime));
    }
    
    @Override
    public boolean isNetworkTreeUpdated(Instant lastUpdateTime) {
        return treeUpdateResponse != null && lastUpdateTime != null
                && lastUpdateTime.isBefore(new Instant(treeUpdateResponse.getTreeGenerationEndTimeMillis()));
    }
    
    @Override
    public Instant getNetworkTreeUpdateTime() {
        return treeUpdateResponse == null ? null : new Instant(treeUpdateResponse.getTreeGenerationEndTimeMillis());
    }
        
    @Override
    public boolean requestNetworkTreeUpdate() {
        if(!isNetworkTreeUpdatePossible()) {
            return false;
        }
        nextForceReloadTime = new DateTime().plusMinutes(reloadFrequencyInMinutes).toInstant();
        NetworkTreeUpdateTimeRequest request = new NetworkTreeUpdateTimeRequest();
        request.setForceRefresh(true);
        log.debug("Sending NetworkTreeUpdateTimeRequest message to request reload of network tree information.");
        jmsTemplate.convertAndSend(JmsApiDirectory.NETWORK_TREE_UPDATE_REQUEST.getQueue().getName(), request);
        return true;
    }
  
    /**
     * For Network Trees found in cache sends request to Network Manager for a new trees and replaces the trees in cache with the
     * trees received from NM.
     */
    private void reloadNetworkTrees() {
        Set<RfnIdentifier> treesToRefresh = networkTreeCache.asMap().keySet();
        if (treesToRefresh.isEmpty()) {
            log.debug("No network trees found in cache, nothing to refresh");
            return;
        }
        log.debug("Refreshing network trees for {}", treesToRefresh);
        try {
            
            Map<RfnIdentifier, RfnGateway>  gatewaysToSendToNM = treesToRefresh.stream()
                    .collect(Collectors.toMap(g -> g, g ->  rfnGatewayService.getGatewayByRfnIdentifier(g)));
            
            List<Node<Pair<Integer, FeatureCollection>>> refreshedTrees = sendRequestToNMForTreeRefresh(gatewaysToSendToNM);
            log.info("Refreshed network trees for {}", refreshedTrees.stream()
                    .map(node -> rfnGatewayService.getGatewayByPaoId(node.getData().getKey()).getRfnIdentifier())
                    .collect(Collectors.toList()));
        } catch (NmCommunicationException e) {
            log.error("Error refreshing network trees for {}", treesToRefresh, e);
        }
    }
    
    @Override
    public List<Node<Pair<Integer, FeatureCollection>>> getNetworkTree(List<Integer> gatewayIds)
            throws NmNetworkException, NmCommunicationException {
       
        if (treeUpdateResponse == null) {
            log.debug("Network tree generation time was not found, sending request to NM to get the time");
            jmsTemplate.convertAndSend(JmsApiDirectory.NETWORK_TREE_UPDATE_REQUEST.getQueue().getName(), new NetworkTreeUpdateTimeRequest());
        }
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
        trees.addAll(sendRequestToNMForTreeRefresh(gatewaysToSendToNM));

        return trees;
    }

    /**
     * Sends request to NM for tree refresh, returns new trees
     */
    private List<Node<Pair<Integer, FeatureCollection>>> sendRequestToNMForTreeRefresh(Map<RfnIdentifier, RfnGateway> gatewaysToSendToNM) throws NmCommunicationException {
        List<Node<Pair<Integer, FeatureCollection>>> trees = new ArrayList<>(); 
        
        Map<Integer, PaoLocation> locations = getLocationsForDevicesAndGateways(gatewaysToSendToNM.values());
        
        log.debug("Getting Network trees from NM for gateways: {}", gatewaysToSendToNM.keySet());
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> metaData = metadataMultiService
                .getMetadataForDeviceRfnIdentifiers(gatewaysToSendToNM.keySet(), Set.of(PRIMARY_FORWARD_TREE));
        
        for (Map.Entry<RfnIdentifier, RfnMetadataMultiQueryResult> data : metaData.entrySet()) {
            log.debug("{} {}", data.getKey(),
                    data.getValue() == null ? "nothing returned" : data.getValue().getMetadatas().size());
            if (data.getValue().isValidResultForMulti(PRIMARY_FORWARD_TREE)) {
                RfnVertex vertex = (RfnVertex) data.getValue().getMetadatas().get(PRIMARY_FORWARD_TREE);
                log.debug("{} Received NM VERTEX", data.getKey());
                log.trace("\n{}", NetworkDebugHelper.print(vertex));
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
    private Node<Pair<Integer, FeatureCollection>> createTree(RfnVertex vertex, Map<Integer, PaoLocation> locations) {
        TreeDebugStatistics yukonNodeStatistics = new TreeDebugStatistics();
        TreeDebugStatistics nmVertexStatistics = new TreeDebugStatistics();

        Node<Pair<Integer, FeatureCollection>> node = createNode(vertex.getRfnIdentifier(), locations, yukonNodeStatistics,
                nmVertexStatistics);
        AtomicInteger numberOfTimesInCopyFunction = new  AtomicInteger(1);
        copy(numberOfTimesInCopyFunction, vertex, node, locations, yukonNodeStatistics, nmVertexStatistics);
        log.debug("numberOfTimesInCopyFunction {}", numberOfTimesInCopyFunction.get());
        networkTreeCache.put(vertex.getRfnIdentifier(), node);
        log.info("New tree created from vertex {} Yukon NODE node {} NM statistics {} Yukon statistics {}",
                vertex.getRfnIdentifier(), node.getData(), nmVertexStatistics, yukonNodeStatistics);
        log.trace("\n{}", node.print());
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
        log.debug("Locations found: {}", locations.size());
        return locations;
    }
    
    /**
     * Creates Node from rfnIdentifier and location
     * @param nmVertexStatistics - collects statistics for debugging purposes
     * @param yukonNodeStatistics  - collects statistics for debugging purposes
     */
    private Node<Pair<Integer, FeatureCollection>> createNode(RfnIdentifier rfnIdentifier, Map<Integer, PaoLocation> locations,
            TreeDebugStatistics yukonNodeStatistics, TreeDebugStatistics nmVertexStatistics) {
        nmVertexStatistics.TOTAL.incrementAndGet();
        yukonNodeStatistics.TOTAL.incrementAndGet();
        if (rfnIdentifier == null) {
            nmVertexStatistics.NULL.incrementAndGet();
            yukonNodeStatistics.NULL.incrementAndGet();
            // NN returned null rfnIdentifier or one of the fields is empty
            return new Node<Pair<Integer, FeatureCollection>>(null);
        }
        if(rfnIdentifier.is_Empty_()) {
            nmVertexStatistics._EMPTY_.incrementAndGet();
            yukonNodeStatistics.NULL.incrementAndGet();
            log.debug("Tree node creation: adding NULL {} to tree, rfnIdentifier is empty.", rfnIdentifier);  
            return new Node<Pair<Integer, FeatureCollection>>(null);
        }
        RfnDevice device = rfnDeviceCreationService.createIfNotFound(rfnIdentifier);
        if (device == null) {
            yukonNodeStatistics.FAILED_TO_CREATE.incrementAndGet();
            // failed to create device
            log.debug("Tree node creation: adding NULL {} to tree, device was not created.", rfnIdentifier);
            return new Node<Pair<Integer, FeatureCollection>>(null);
        }
        // if device is not found create device
        int deviceId = device.getPaoIdentifier().getPaoId();
        PaoLocation location = locations.get(deviceId);
        if(location == null) {
            yukonNodeStatistics.NO_LOCATION.incrementAndGet();
            log.debug("Tree node creation: adding device {} id {} without location, location is not found.", rfnIdentifier, deviceId);
        }
        // if no location in Yukon database featureCollection will be null
        FeatureCollection featureCollection = location == null ? null : paoLocationService.getFeatureCollection(location);
        return new Node<Pair<Integer, FeatureCollection>>(Pair.of(deviceId, featureCollection));
    }
    
    /**
     * Copies RfnVertex to Node
     */
    private void copy(AtomicInteger numberOfTimesInCopyFunction, RfnVertex vertex, Node<Pair<Integer, FeatureCollection>> parent, Map<Integer, PaoLocation> locations,
            TreeDebugStatistics yukonNodeStatistics, TreeDebugStatistics nmVertexStatistics) {
        numberOfTimesInCopyFunction.incrementAndGet();
        for (Iterator<RfnVertex> it = vertex.getChildren().iterator(); it.hasNext();) {
            RfnVertex nextNode = it.next();
            Node<Pair<Integer, FeatureCollection>> child = createNode(nextNode.getRfnIdentifier(), locations, yukonNodeStatistics,
                    nmVertexStatistics);
            parent.addChild(child);
            copy(numberOfTimesInCopyFunction, nextNode, child, locations, yukonNodeStatistics, nmVertexStatistics);
        }
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
       
                
                if (object instanceof NetworkTreeUpdateTimeResponse) {
                    NetworkTreeUpdateTimeResponse response = (NetworkTreeUpdateTimeResponse) object;
                    
                    Instant lastUpdateTime =
                            persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.DYNAMIC_RFN_DEVICE_DATA_LAST_UPDATE_TIME);
                    
                    if (lastUpdateTime == null
                            || response.getTreeGenerationEndTimeMillis() > lastUpdateTime.getMillis()) {
                        updateDeviceToGatewayMapping(new Instant(response.getTreeGenerationEndTimeMillis()));
                        persistedSystemValueDao.setValue(PersistedSystemValueKey.DYNAMIC_RFN_DEVICE_DATA_LAST_UPDATE_TIME, new Instant(response.getTreeGenerationEndTimeMillis()));
                    }
                    
                    if (treeUpdateResponse == null || response.getTreeGenerationEndTimeMillis() > treeUpdateResponse.getTreeGenerationEndTimeMillis()) {
                        log.info(
                                "Received Network Tree Update Time - Start {} End {} Next automatic refresh {} Next forced refresh after {}",
                                format(response.getTreeGenerationStartTimeMillis()),
                                format(response.getTreeGenerationEndTimeMillis()),
                                format(response.getNextScheduledRefreshTimeMillis()),
                                format(response.getNoForceRefreshBeforeTimeMillis()));

                        //Reloads only cached trees
                        reloadNetworkTrees();
                    }
                    treeUpdateResponse = response;                   
                } 
            } catch (JMSException e) {
                log.warn("Unable to extract NetworkTreeUpdateTimeResponse from message", e);
            }
        }   
    }
    
    /**
     * Sends message to NM to get device to gateway mapping information for all gateways. When message is received the device to
     * gateway mapping is persisted in DynamicRfnDeviceData.
     */
    private void updateDeviceToGatewayMapping(Instant treeGenerationEndTime) {
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        String gatewayNames = gateways.stream().map(gateway -> gateway.getName()).collect(Collectors.joining(", "));
        log.info("Sending request to NM for device to gateway mapping information for {}", gatewayNames);
        Map<RfnIdentifier, RfnDevice> gatewayIds = gateways.stream()
                .collect(Collectors.toMap(gateway -> gateway.getRfnIdentifier(), gateway -> gateway));
        try {
            Map<RfnIdentifier, RfnMetadataMultiQueryResult> response = metadataMultiService
                    .getMetadataForGatewayRfnIdentifiers(gatewayIds.keySet(),
                            Set.of(PRIMARY_FORWARD_GATEWAY, PRIMARY_FORWARD_DESCENDANT_COUNT));
            Set<DynamicRfnDeviceData> deviceData = new HashSet<>();
            response.forEach((deviceRfnIdentifier, queryResult) -> {
                RfnDevice device = rfnDeviceCreationService.createIfNotFound(deviceRfnIdentifier);
                // Li confirmed PRIMARY_FORWARD_GATEWAY and PRIMARY_FORWARD_DESCENDANT_COUNT will always be returned
                if (device != null && queryResult.isValidResultForMulti(PRIMARY_FORWARD_GATEWAY)
                        && queryResult.isValidResultForMulti(PRIMARY_FORWARD_DESCENDANT_COUNT)) {
                    RfnIdentifier gatewayRfnIdentifier = (RfnIdentifier) queryResult.getMetadatas().get(PRIMARY_FORWARD_GATEWAY);
                    // if gateway doesn't exists in Yukon, RfnIdentifier is not enough information to create gateway
                    if (gatewayIds.containsKey(gatewayRfnIdentifier)) {
                        int descendantCount = (Integer) queryResult.getMetadatas().get(PRIMARY_FORWARD_DESCENDANT_COUNT);
                        DynamicRfnDeviceData data = new DynamicRfnDeviceData(device, gatewayIds.get(gatewayRfnIdentifier),
                                descendantCount, treeGenerationEndTime);
                        deviceData.add(data);
                    }
                }
            });

            log.info("Updating device to gateway mapping information for {} devices {}", gatewayNames, deviceData.size());
            rfnDeviceDao.saveDynamicRfnDeviceData(deviceData);
            log.info("Updated device to gateway mapping information for {} devices {}", gatewayNames, deviceData.size());

        } catch (NmCommunicationException e) {
            log.error("Error while trying to send request to NM for device to gateway mapping information.", e);
        }
    }

    private String format(long millis) {
        return new Instant(millis).toString(df.withZone(DateTimeZone.getDefault()));
    }
}

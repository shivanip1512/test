package com.cannontech.common.rfn.simulation.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetReply;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetReplyType;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetRequest;
import com.cannontech.amr.rfn.message.status.RfnStatusArchiveRequest;
import com.cannontech.amr.rfn.message.status.type.DemandResetStatus;
import com.cannontech.amr.rfn.message.status.type.DemandResetStatusCode;
import com.cannontech.amr.rfn.message.status.type.MeterInfo;
import com.cannontech.amr.rfn.message.status.type.MeterInfoStatus;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoDistance;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.pao.service.LocationService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMulti;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResult;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiQueryResultType;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiRequest;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponse;
import com.cannontech.common.rfn.message.metadatamulti.RfnMetadataMultiResponseType;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.network.NeighborFlagType;
import com.cannontech.common.rfn.message.network.ParentData;
import com.cannontech.common.rfn.message.network.RfnNeighborDataReply;
import com.cannontech.common.rfn.message.network.RfnNeighborDataReplyType;
import com.cannontech.common.rfn.message.network.RfnNeighborDataRequest;
import com.cannontech.common.rfn.message.network.RfnParentReply;
import com.cannontech.common.rfn.message.network.RfnParentReplyType;
import com.cannontech.common.rfn.message.network.RfnParentRequest;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeCommStatus;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.message.node.NodeNetworkInfo;
import com.cannontech.common.rfn.message.node.NodeType;
import com.cannontech.common.rfn.message.node.WifiSecurityType;
import com.cannontech.common.rfn.message.node.WifiSuperMeterData;
import com.cannontech.common.rfn.message.route.RfnRoute;
import com.cannontech.common.rfn.message.route.RouteData;
import com.cannontech.common.rfn.message.route.RouteFlag;
import com.cannontech.common.rfn.message.tree.NetworkTreeUpdateTimeRequest;
import com.cannontech.common.rfn.message.tree.NetworkTreeUpdateTimeResponse;
import com.cannontech.common.rfn.message.tree.RfnVertex;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.rfn.simulation.SimulatedNmMappingSettings;
import com.cannontech.common.rfn.simulation.service.NetworkTreeSimulatorService;
import com.cannontech.common.rfn.simulation.service.NmNetworkSimulatorService;
import com.cannontech.common.rfn.simulation.service.PaoLocationSimulatorService;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class NmNetworkSimulatorServiceImpl implements NmNetworkSimulatorService {
    private static final String requestQueue = "com.eaton.eas.yukon.networkmanager.network.data.request";
    public static final int incomingMessageWaitMillis = 1000;
    
    private final static Logger log = YukonLogManager.getLogger(NmNetworkSimulatorServiceImpl.class);
   
    private static final double DISTANCE_IN_MILES = 10;
                
    private SimulatedNmMappingSettings settings;
    
    private volatile boolean isRunning;
        
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private AttributeService attributeService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private PaoLocationSimulatorService paoLocationSimulatorService;
    @Autowired private NetworkTreeSimulatorService networkTreeSimulatorService;
   
    private JmsTemplate jmsTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> task;
    String templatePrefix;
    private Set<PaoType> wiFiSuperMeters = Set.of(PaoType.WRL420CL, PaoType.WRL420CD);
    private NetworkTreeUpdateTimeResponse networkTreeUpdateTimeResponse;
    
    private final Cache<RfnIdentifier, RfnVertex> vertexCache =
            CacheBuilder.newBuilder().expireAfterWrite(8, TimeUnit.HOURS).build();
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(incomingMessageWaitMillis);
    }
    
    @Override
    public void start(SimulatedNmMappingSettings settings) {
        updateSettings(settings);
        isRunning = true;
        task = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Object message = jmsTemplate.receive(requestQueue);
                    if (message != null) {
                        ObjectMessage requestMessage = (ObjectMessage) message;
                        if (requestMessage.getObject() instanceof RfnParentRequest) {
                            RfnParentRequest request = (RfnParentRequest) requestMessage.getObject();
                            RfnParentReply reply = getParent(request.getRfnIdentifier());
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), reply);
                        } else if (requestMessage.getObject() instanceof RfnNeighborDataRequest) {
                            RfnNeighborDataRequest request = (RfnNeighborDataRequest) requestMessage.getObject();
                            RfnNeighborDataReply reply = getNeighbors(request.getRfnIdentifier());
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), reply);
                        }
                    }
                    receiveMetaDataMultiMessage();
                    receiveDemandResetMessage();
                    receiveNetworkTreeUpdateTimeMessage();
                    
                } catch (Exception e) {
                    log.error("Error occurred in NM Network Simulator.", e);
                }
            }

            private void receiveNetworkTreeUpdateTimeMessage() throws JMSException {
                Object message = jmsTemplate.receive(JmsApiDirectory.NETWORK_TREE_UPDATE_REQUEST.getQueue().getName());
                if (message != null) {
                    ObjectMessage requestMessage = (ObjectMessage) message;
                    if (requestMessage.getObject() instanceof NetworkTreeUpdateTimeRequest) {
                        NetworkTreeUpdateTimeRequest request = (NetworkTreeUpdateTimeRequest) requestMessage.getObject();
                        log.debug("NetworkTreeUpdateTimeRequest received {}", request);
                       
                        if(!request.isForceRefresh() && networkTreeUpdateTimeResponse != null){
                            jmsTemplate.convertAndSend(JmsApiDirectory.NETWORK_TREE_UPDATE_RESPONSE.getQueue().getName(), networkTreeUpdateTimeResponse);
                            return;
                        }
              
                       
                        if(request.isForceRefresh()) {
                            vertexCache.asMap().keySet().forEach(gateway -> {
                                RfnVertex vertex = networkTreeSimulatorService
                                        .buildVertex(rfnGatewayService.getGatewayByRfnIdentifier(gateway));
                                vertexCache.put(gateway, vertex);
                            });
         
                        }
                        networkTreeUpdateTimeResponse = new NetworkTreeUpdateTimeResponse();
                        networkTreeUpdateTimeResponse.setTreeGenerationStartTimeMillis(System.currentTimeMillis());
                        networkTreeUpdateTimeResponse.setTreeGenerationEndTimeMillis(new DateTime().plusMinutes(1).getMillis());
                        networkTreeUpdateTimeResponse.setNextScheduledRefreshTimeMillis(new DateTime().plusMinutes(2).getMillis());
                        networkTreeUpdateTimeResponse.setNoForceRefreshBeforeTimeMillis(new DateTime().plusMinutes(3).getMillis());
                        jmsTemplate.convertAndSend(JmsApiDirectory.NETWORK_TREE_UPDATE_RESPONSE.getQueue().getName(), networkTreeUpdateTimeResponse);
                    }
                }
            }

            private void receiveDemandResetMessage() throws JMSException {
                Object demandResetMessage = jmsTemplate.receive(JmsApiDirectory.RFN_METER_DEMAND_RESET.getQueue().getName());
                if (demandResetMessage != null) {
                    ObjectMessage requestMessage = (ObjectMessage) demandResetMessage;
                    if (requestMessage.getObject() instanceof RfnMeterDemandResetRequest) {
                        RfnMeterDemandResetRequest request = (RfnMeterDemandResetRequest) requestMessage.getObject();
                        log.debug("RfnMeterDemandResetRequest meter identifiers {}", request.getRfnMeterIdentifiers().size());
                      
                        RfnMeterDemandResetReply reply = new RfnMeterDemandResetReply();
                        Map<RfnIdentifier, RfnMeterDemandResetReplyType> replies = 
                                request.getRfnMeterIdentifiers().stream()
                                    .collect(Collectors.toMap(Function.identity(), identifier -> RfnMeterDemandResetReplyType.OK));
                        reply.setReplyTypes(replies);
                        jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), reply);

                        String statusCode = yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.DEMAND_RESET_STATUS_ARCHIVE);

                        sendDemandResetStatusArchiveRequest(request.getRfnMeterIdentifiers(), null, DemandResetStatusCode.valueOf(statusCode));
                    }
                }
            }

            private void receiveMetaDataMultiMessage() throws JMSException {
                Object metaDataMultiMessage = jmsTemplate.receive(JmsApiDirectory.RF_METADATA_MULTI.getQueue().getName());
                if (metaDataMultiMessage != null) {
                    ObjectMessage requestMessage = (ObjectMessage) metaDataMultiMessage;
                    if (requestMessage.getObject() instanceof RfnMetadataMultiRequest) {
                        RfnMetadataMultiRequest request = (RfnMetadataMultiRequest) requestMessage.getObject();
                        for (RfnMetadataMultiResponse reply : getPartitionedMetadataMultiResponse(request)) {
                            log.info("RfnMetadataMultiRequest identifier: {} segment: {} response: {} sending response on {}",
                                    request.getRequestID(), reply.getSegmentNumber(), reply.getResponseType(),
                                    requestMessage.getJMSReplyTo());
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), reply);
                        }
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        log.info("Started NM Network Simulator");
    }
        

    private List<RfnMetadataMultiResponse> getPartitionedMetadataMultiResponse(RfnMetadataMultiRequest request) {
        List<RfnMetadataMultiResponse> responses = new ArrayList<>();
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> results = getResults(request);
        List<List<RfnIdentifier>> parts = Lists.partition(Lists.newArrayList(results.keySet()), 25000);
        log.debug("--Split identifiers {} into {} parts", results.size(), parts.size());
    
        for (int i = 0; i < parts.size(); i++) {
            RfnMetadataMultiResponse response = new RfnMetadataMultiResponse(request.getRequestID(), parts.size(), i + 1);
            response.setResponseType(settings.getMetadataResponseType());
            response.setQueryResults(new HashMap<>());
            parts.get(i).forEach(identifier -> response.getQueryResults().put(identifier, results.get(identifier)));
            responses.add(response);
            log.debug("--Created response {} (of {}) query results {} for {}", response.getSegmentNumber(),
                response.getTotalSegments(), response.getQueryResults().size(), request.getRfnMetadatas());
        }
        
        if(parts.isEmpty()) {
            RfnMetadataMultiResponse response = new RfnMetadataMultiResponse(request.getRequestID(), parts.size(), 1);
            // example: If query PRIMARY_FORWARD_GATEWAY for a gateway and there is not devices associated with this gateway, the
            // return is YUKON_INPUT_ERROR, according to Li
            response.setResponseType(RfnMetadataMultiResponseType.YUKON_INPUT_ERROR);
            response.setQueryResults(new HashMap<>());
            responses.add(response);
            log.debug("--Created response {} (of {}) query results {} for {}", response.getSegmentNumber(),
                    response.getTotalSegments(), response.getQueryResults().size(), request.getRfnMetadatas());
        }
        return responses;
    }
    
    private Map<RfnIdentifier, RfnMetadataMultiQueryResult> getResults(RfnMetadataMultiRequest request) {
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> results = new HashMap<>();
        Set<RfnIdentifier> rfnIdentifiers = getRfnIdentifiers(request);

        List<DynamicRfnDeviceData> data = rfnDeviceDao.getAllDynamicRfnDeviceData();
        Map<RfnIdentifier, DynamicRfnDeviceData> deviceDataMap = data.stream()
                .collect(Collectors.toMap(d -> d.getDevice().getRfnIdentifier(), d -> d));
        log.debug("devicesToGatewayMap size {}", deviceDataMap.size());
        log.debug("rfnIdentifiers size {}", rfnIdentifiers.size());

        for (RfnIdentifier device : rfnIdentifiers) {
            try {
                RfnDevice rfnDevice = rfnDeviceDao.getDeviceForExactIdentifier(device);
                RfnIdentifier gateway = null;
                if (deviceDataMap.get(device) != null) {
                    gateway = deviceDataMap.get(device).getGateway().getRfnIdentifier();
                }
                for (RfnMetadataMulti multi : request.getRfnMetadatas()) {
                    if (multi == RfnMetadataMulti.REVERSE_LOOKUP_NODE_COMM && gateway != null) {
                        addObjectToResult(results, device, multi, getNodeComm(device, gateway));
                    } else if (multi == RfnMetadataMulti.NODE_DATA) {
                        addObjectToResult(results, device, multi, getNodeData(rfnDevice));
                    } else if (multi == RfnMetadataMulti.PRIMARY_FORWARD_GATEWAY && gateway != null) {
                        addObjectToResult(results, device, multi, gateway);
                    } else if (multi == RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA) {
                        addObjectToResult(results, device, multi, getNeighborData());
                    } else if (multi == RfnMetadataMulti.PRIMARY_FORWARD_TREE) {
                        addObjectToResult(results, device, multi, getVertex(rfnDevice));
                    } else if (multi == RfnMetadataMulti.PRIMARY_FORWARD_ROUTE_DATA && gateway != null) {
                        addObjectToResult(results, device, multi, getRouteData(rfnDevice, gateway));
                    } else if (multi == RfnMetadataMulti.PRIMARY_FORWARD_DESCENDANT_COUNT && gateway != null) {
                        Integer descendantCount = deviceDataMap.get(device).getDescendantCount();
                        addObjectToResult(results, device, multi, descendantCount);
                    } else if (multi == RfnMetadataMulti.PRIMARY_FORWARD_ROUTE) {
                        addObjectToResult(results, device, multi, getRoute(rfnDevice.getRfnIdentifier(), gateway));
                    } else if (multi == RfnMetadataMulti.NODE_NETWORK_INFO) {
                        addObjectToResult(results, device, multi, getNetworkInfo(rfnDevice.getRfnIdentifier()));
                    } else if (multi == RfnMetadataMulti.READY_BATTERY_NODE_COUNT) {
                        addObjectToResult(results, device, multi, 3);
                    } else if (multi == RfnMetadataMulti.NEIGHBOR_COUNT) {
                        addObjectToResult(results, device, multi, 4);
                    }
                }
            } catch (Exception e) {
                log.error("Error generating results for RfnMetadataMultiQueryResult for {} ", device, e);
            }
        }
        log.debug("Results generation ended");
        return results;
    }

    private NodeNetworkInfo getNetworkInfo(RfnIdentifier rfnIdentifier) {
        NodeNetworkInfo info = new NodeNetworkInfo();
        info.setHostname("Hostname");
        info.setIpv6Address("FD30:0000:0000:0001:0214:08FF:FE0A:BF91");
        info.addNodeGroupName("Group 1");
        info.addNodeName("Group 1");
        if (new Random().nextBoolean()) {
            info.addNodeGroupName("Group 2");
            info.addNodeName("Group 2");
        }
        if (new Random().nextBoolean()) {
            info.addNodeGroupName("Group 3");
            info.addNodeName("Group 3");
        }
        return info;
    }

    private void addObjectToResult(Map<RfnIdentifier, RfnMetadataMultiQueryResult> results, RfnIdentifier device,
            RfnMetadataMulti multi, Object object) {
        if (!results.containsKey(device)) {
            RfnMetadataMultiQueryResult newResult = new RfnMetadataMultiQueryResult();
            newResult.setResultType(RfnMetadataMultiQueryResultType.OK);
            newResult.setMetadatas(new HashMap<>());
            results.put(device, newResult);
        }
        RfnMetadataMultiQueryResult result = results.get(device);
        result.getMetadatas().put(multi, object);
    }

    private RouteData getRouteData(RfnDevice rfnDevice,  RfnIdentifier gateway) {
        RouteData routeData = new RouteData();
        routeData.setDestinationAddress(settings.getRouteData().getDestinationAddress());
        routeData.setHopCount((short)getRandomNumberInRange(1, 25));
        routeData.setNextHopAddress(settings.getRouteData().getNextHopAddress());
        routeData.setRouteColor(settings.getRouteData().getRouteColor());
        routeData.setRouteDataTimeStamp(settings.getRouteData().getRouteDataTimeStamp());
        routeData.setRouteFlags(settings.getRouteData().getRouteFlags());
        routeData.setRouteTimeout(settings.getRouteData().getRouteTimeout());
        routeData.setTotalCost(settings.getRouteData().getTotalCost());
        //for simulator next hop is always gateway
        routeData.setNextHopRfnIdentifier(gateway);
        return routeData;
    }

    private NeighborData getNeighborData() {
        /*
         * Add to test "Unknown" option on comprehensive map, select all for filters and color code by link quality
         * if(new Random().nextBoolean()) {
         * return null;
         * }
         */
        NeighborData neighborData = new NeighborData();
        List<Integer> linkCost = Arrays.asList(1, 2, 3, 4, 5);
        int randomElement = linkCost.get(new Random().nextInt(linkCost.size()));
        neighborData.setNeighborLinkCost((float) randomElement);
        // Generates random short between 1 and 6 for the ExtBand
        short randomEtxBand = (short) (Math.random() * (5) + 1);
        neighborData.setEtxBand(randomEtxBand);
        List<Integer> numSamples = Arrays.asList(49, 50, 51);
        randomElement = numSamples.get(new Random().nextInt(numSamples.size()));
        neighborData.setNumSamples(randomElement);
        neighborData.setNeighborMacAddress(settings.getNeighborData().getNeighborAddress());
        neighborData.setNeighborDataTimestamp(settings.getNeighborData().getNeighborDataTimestamp());
        neighborData.setNeighborLinkCost(settings.getNeighborData().getNeighborLinkCost());
        return neighborData;
    }

    private NodeData getNodeData(RfnDevice rfnDevice) {
        NodeData node = new NodeData();
        node.setFirmwareVersion("R2.1.5Wp");
        node.setHardwareVersion("1.1.1 (Sim)");
        node.setInNetworkTimestamp(1517588257267L);
        node.setMacAddress("11:22:33:44:91:11");
        node.setNetworkAddress("00C36E09081400");
        node.setNodeType(NodeType.ELECTRIC_NODE);
        node.setProductNumber("123456789 (Sim)");
        node.setNodeSerialNumber("4260060913");
        node.setSecondaryModuleFirmwareVersion("R2.2.0Wp");
        if (wiFiSuperMeters.contains(rfnDevice.getPaoIdentifier().getPaoType())) {
            node.setWifiSuperMeterData(getSuperMeterData());
        }
        return node;
    }

    private Set<RfnIdentifier> getRfnIdentifiers(RfnMetadataMultiRequest request) {
        Set<RfnIdentifier> rfnIdentifiers = new HashSet<>();
        // by gateway identifier
        if (!CollectionUtils.isEmpty(request.getPrimaryForwardNodesForGatewayRfnIdentifiers())) {
            for (RfnIdentifier device : request.getPrimaryForwardNodesForGatewayRfnIdentifiers()) {
                RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(device);
                // find nodes and add to the identifiers for lookup
                List<RfnIdentifier> devices = getDevicesForGateway(gateway);
                rfnIdentifiers.addAll(devices);
            }
        } else if (!CollectionUtils.isEmpty(request.getRfnIdentifiers())) {
            rfnIdentifiers.addAll(request.getRfnIdentifiers());
        }
        return rfnIdentifiers;
    }

    private RfnVertex getVertex(RfnDevice gateway) {
        RfnVertex vertex = vertexCache.getIfPresent(gateway.getRfnIdentifier());
        if (vertex == null) {
            vertex = networkTreeSimulatorService.buildVertex(gateway);
            log.debug("Created vertex for {}", gateway);
            vertexCache.put(gateway.getRfnIdentifier(), vertex);
        } else {
            log.debug("Found cached vertex for {}", gateway);
        }
        return vertex;
    }

    private WifiSuperMeterData getSuperMeterData() {
        WifiSuperMeterData superMeterData = new WifiSuperMeterData();
        superMeterData.setConfiguredApBssid("12:34:56:78:90:ab");
        superMeterData.setConnectedApBssid("ab:cd:ef:01:23:45");
        superMeterData.setApSsid("ExampleUtilityISP");
        superMeterData.setSecurityType(WifiSecurityType.WPA2_PERSONAL);
        superMeterData.setVirtualGatewayIpv6Address("FD30:0000:0000:0001:0214:08FF:FE0A:BF91");
        return superMeterData;
    }

    private List<RfnIdentifier> getDevicesForGateway(RfnDevice gateway) {
        // int connectedNodesWarningLimit =
        // globalSettingDao.getInteger(GlobalSettingType.GATEWAY_CONNECTED_NODES_WARNING_THRESHOLD);
        // since mapping simulator is now configurable (we can map the # of devices to gateway), the limitation is removed here
        return rfnDeviceDao.getRfnIdentifiersForGateway(gateway.getPaoIdentifier().getPaoId(), 100000);
    }

    private NodeComm getNodeComm(RfnIdentifier device, RfnIdentifier gateway) {
        boolean isReady = new Random().nextBoolean();
        NodeComm comm = new NodeComm();
        comm.setNodeCommStatusTimestamp(1517588257267L);
        comm.setNodeCommStatus(isReady ? NodeCommStatus.READY : NodeCommStatus.NOT_READY);
        comm.setGatewayRfnIdentifier(gateway);
        comm.setDeviceRfnIdentifier(device);
        return comm;
    }

    @Override
    public void stop() {
        log.info("Stopping NM Network Simulator");
        task.cancel(true);
        isRunning = false;
    }

    @Override
    public void updateSettings(SimulatedNmMappingSettings settings) {
        this.settings = settings;
    }

    @Override
    public SimulatedNmMappingSettings getSettings() {
        if (settings == null) {
            SimulatedNmMappingSettings simulatedNmMappingSettings = new SimulatedNmMappingSettings();
            
            //Metadata
            simulatedNmMappingSettings.setMetadataResponseType(RfnMetadataMultiResponseType.valueOf((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_METADATA_RESPONSE_TYPE.getDefaultValue()));
            simulatedNmMappingSettings.setMetadataQueryResponseType(RfnMetadataMultiQueryResultType.valueOf((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_METADATA_DEVICE_RESPONSE_TYPE.getDefaultValue()));
            simulatedNmMappingSettings.setMetadataResponseString((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_METADATA_RESPONSE_STRING.getDefaultValue());

            //NeighborData
            com.cannontech.common.rfn.message.network.NeighborData neighborData = new com.cannontech.common.rfn.message.network.NeighborData();
            neighborData.setNeighborAddress((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_ADDR.getDefaultValue());
            neighborData.setEtxBand((short) ((int)YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_EXT_BAND.getDefaultValue()));
            neighborData.setLastCommTime(new Date().getTime());
            neighborData.setLinkPower((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_LINK_POW.getDefaultValue());
            neighborData.setLinkRate((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_LINK_RATE.getDefaultValue());
            neighborData.setNeighborDataTimestamp(new Date().getTime());
            //flags
            Set<NeighborFlagType> types = new HashSet<>();
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_PRIM_FORW_ROUTE.getDefaultValue()) {
                types.add(NeighborFlagType.PF);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_PRIM_REV_ROUTE.getDefaultValue()) {    
                types.add(NeighborFlagType.PR);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_SEC_ALT_GATEWAY.getDefaultValue()) {    
                types.add(NeighborFlagType.S2);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_FLOAT_NEIGHB.getDefaultValue()) {    
                types.add(NeighborFlagType.F);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_IGNORED_NEIGHB.getDefaultValue()) {    
                types.add(NeighborFlagType.IN);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_BATTERY_NEIGHB.getDefaultValue()) {    
                types.add(NeighborFlagType.BN);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_SEC_SERV_GATEWAY.getDefaultValue()) {    
                types.add(NeighborFlagType.S1);
            }
            neighborData.setNeighborFlags(types);
            
            neighborData.setNeighborLinkCost((float) ((double)YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_LINK_COST.getDefaultValue()));
            neighborData.setNextCommTime(new Date().getTime());
            neighborData.setNumSamples((int) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_NUM_SAMPLES.getDefaultValue());
            neighborData.setSerialNumber("123");
            simulatedNmMappingSettings.setNeighborData(neighborData);
    
            //RouteData
            RouteData routeData = new RouteData();
            routeData.setHopCount((short) ((int)YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_HOP_COUNT.getDefaultValue()));
            routeData.setRouteColor((short) ((int)YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_COLOR.getDefaultValue()));
            routeData.setRouteDataTimeStamp(new Date().getTime());
            //flags
            Set<RouteFlag> routeTypes = new HashSet<>();
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_FORW_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlag.ROUTE_FLAG_PRIMARY_FORWARD);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_REV_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlag.ROUTE_FLAG_PRIMARY_REVERSE);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_BATTERY_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlag.ROUTE_FLAG_BATTERY);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_START_GC.getDefaultValue()) {    
                routeTypes.add(RouteFlag.ROUTE_FLAG_ROUTE_START_GC);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_REM_UPDATE.getDefaultValue()) {    
                routeTypes.add(RouteFlag.ROUTE_FLAG_ROUTE_REMEDIAL_UPDATE);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_IGNORED_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlag.ROUTE_FLAG_IGNORED);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_VALID_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlag.ROUTE_FLAG_VALID);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_TIMED_OUT.getDefaultValue()) {    
                routeTypes.add(RouteFlag.ROUTE_FLAG_TIMED_OUT);
            }
            routeData.setRouteFlags(routeTypes);
            
            routeData.setRouteTimeout(new Date().getTime());
            routeData.setTotalCost((short) ((int)YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_COST.getDefaultValue()));
            simulatedNmMappingSettings.setRouteData(routeData);
    
            //ParentData
            ParentData parentData = new ParentData();
            parentData.setNodeSN((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PARENT_SN.getDefaultValue());
            simulatedNmMappingSettings.setParentData(parentData);
            
            simulatedNmMappingSettings.setNeighborReplyType(RfnNeighborDataReplyType.valueOf((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHBOR_DATA_REPLY_TYPE.getDefaultValue()));
            simulatedNmMappingSettings.setParentReplyType(RfnParentReplyType.valueOf((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PARENT_REPLY_TYPE.getDefaultValue()));
            
            //Network Tree
            simulatedNmMappingSettings.setEmptyNullPercent(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_PERCENT_NULL));
            simulatedNmMappingSettings.setMinHop(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_MIN_HOP));
            simulatedNmMappingSettings.setMaxHop(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_MAX_HOP));
            simulatedNmMappingSettings.setNodesOneHop(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_NODES_ONE_HOP));
            simulatedNmMappingSettings.setNumberOfDevicesPerGateway(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_NUM_DEVICES_PER_GW));
            simulatedNmMappingSettings.setCreateGateways(yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_CREATE_GW));
            
            settings = simulatedNmMappingSettings;
        }
        return settings;
    }
    
    /**
     * This method is only used for water meters
     * Water node�s parent must always be an electric node. Electric nodes are devices such as � electric meters, LCRs,
     * DA devices (CBC?), RF relays. Water meters are attached to battery nodes that is because it is not always
     * guaranteed to have an available powerline in places they want to measure water usage
     * The parent of the water meter can be LCR, CBC, RELAY, RF METER
     */
    public RfnParentReply getParent(RfnIdentifier identifier) {
        RfnDevice device= rfnDeviceDao.getDeviceForExactIdentifier(identifier);
        // get 10 closest neighbors
        List<RfnDevice> neighbors = getNeighbors(device, 10);
        // parent is one of the neighbors that is not a gateway or a water meter
        RfnDevice parent = neighbors.stream().filter(e -> !e.getPaoIdentifier().getPaoType().isRfGateway()
            && !e.getPaoIdentifier().getPaoType().isWaterMeter()).findFirst().orElse(null);
        if(parent == null){
            return null;
        }
        
        RfnParentReply reply = new RfnParentReply();
        reply.setReplyType(settings.getParentReplyType());
        reply.setParentData(getParentDataFromSettings(parent.getRfnIdentifier()));
        reply.setRfnIdentifier(parent.getRfnIdentifier());
        return reply;
    }
    
    /**
     * Creates a response with the route information.
     */
    public RfnRoute getRoute(RfnIdentifier identifier, RfnIdentifier gateway) {
        RfnDevice device = rfnDeviceDao.getDeviceForExactIdentifier(identifier);
        int max = getRandomNumberInRange(2, 8);
        List<RfnDevice> neighbors = getNeighbors(device, max);
        RfnRoute route = new RfnRoute();
        Integer nodeNullPercent = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_PERCENT_NULL);
        neighbors.forEach(neighbor -> {
            route.add(new Random().nextInt(100) < nodeNullPercent ? getNullIdentifier(neighbor.getRfnIdentifier()) : neighbor
                    .getRfnIdentifier());
        });
        route.add(gateway);
        return route;
    }
    
    private RfnIdentifier getNullIdentifier(RfnIdentifier identifier) {
        if(new Random().nextBoolean()) {
            return null;
        }
        return new RfnIdentifier("_EMPTY_", identifier.getSensorManufacturer(), identifier.getSensorModel());
        
    }
    
    /**
     * Creates a response with the neighbor information.
     */
    public RfnNeighborDataReply getNeighbors(RfnIdentifier identifier) {
        RfnDevice device= rfnDeviceDao.getDeviceForExactIdentifier(identifier);
        log.info("device="+device);
        int max = getRandomNumberInRange(2, 3);
        List<RfnDevice> neighbors = getNeighbors(device, max);

        RfnNeighborDataReply reply = new RfnNeighborDataReply();
        Set<com.cannontech.common.rfn.message.network.NeighborData> neighborData = getNeighborDataFromSettings(neighbors);
        
        reply.setReplyType(settings.getNeighborReplyType());
        reply.setRfnIdentifier(device.getRfnIdentifier());
        reply.setNeighborData(neighborData);
        log.info("identifier="+ identifier+" neighborData="+neighborData);
        
        return reply;
    }
    
    private ParentData getParentDataFromSettings(RfnIdentifier rfnIdentifier){
        ParentData data = new ParentData();
        data.setNodeMacAddress(settings.getParentData().getNodeMacAddress());
        data.setNodeSN(settings.getParentData().getNodeSN());
        data.setRfnIdentifier(rfnIdentifier);
        return data;
    }
    
    private  Set<com.cannontech.common.rfn.message.network.NeighborData> getNeighborDataFromSettings(List<RfnDevice> neighbors){
        Set<com.cannontech.common.rfn.message.network.NeighborData> neighborData = new HashSet<>();
        for (RfnDevice device : neighbors) {
            com.cannontech.common.rfn.message.network.NeighborData data = new com.cannontech.common.rfn.message.network.NeighborData();
            data.setEtxBand(settings.getNeighborData().getEtxBand());
            data.setLastCommTime(settings.getNeighborData().getLastCommTime());
            data.setLinkPower(settings.getNeighborData().getLinkPower());
            data.setLinkRate(settings.getNeighborData().getLinkRate());
            data.setNeighborAddress(settings.getNeighborData().getNeighborAddress());
            data.setNeighborFlags(settings.getNeighborData().getNeighborFlags());            
            data.setNeighborLinkCost(settings.getNeighborData().getNeighborLinkCost());
            data.setNextCommTime(settings.getNeighborData().getNextCommTime());
            data.setNumSamples(settings.getNeighborData().getNumSamples());
            data.setRfnIdentifier(device.getRfnIdentifier());
            data.setSerialNumber(device.getRfnIdentifier().getSensorSerialNumber());
            neighborData.add(data);
        }
        return neighborData;
    }
    
    private List<RfnDevice> getNeighbors(RfnDevice device, int max) {
        PaoLocation location = paoLocationDao.getLocation(device.getPaoIdentifier().getPaoId());
        List<PaoLocation> locations = paoLocationDao.getLocations(Origin.SIMULATOR);
        return getNearbyLocations(locations, location, DISTANCE_IN_MILES,
            DistanceUnit.MILES, max);
    }
        
    public List<RfnDevice> getNearbyLocations(List<PaoLocation> locations, PaoLocation location, double distance, DistanceUnit unit, int max) {
        
        List<PaoDistance> nearby = new ArrayList<>();
        Map<Integer, RfnDevice> devices = new HashMap<>();
        for (PaoLocation current : locations) {
            if (location.equals(current)) {
                continue;
            }
            double distanceTo = location.distanceTo(current, unit);
            if (distanceTo <= distance) {
                LiteYukonPAObject pao = databaseCache.getAllPaosMap().get(current.getPaoIdentifier().getPaoId());
                try{
                    RfnDevice device = rfnDeviceDao.getDeviceForId(pao.getYukonID());
                    if(RfnManufacturerModel.of(device.getRfnIdentifier()) != null) {
                        nearby.add(PaoDistance.of(pao, distanceTo, unit, current)); 
                        devices.put(device.getPaoIdentifier().getPaoId(), device);
                    }
                } catch (Exception e) {
                    //device doesn't have RFN identifier, example - template
                }
                if(nearby.size() == max) {
                    break;
                }
            }
        }
        
        Collections.sort(nearby, LocationService.ON_DISTANCE);
        
        return nearby.stream().map(d-> devices.get(d.getPao().getLiteID())).collect(Collectors.toList());
    }
    
    @Override
    public void sendDemandResetStatusArchiveRequest(Set<RfnIdentifier> identifiers, Integer limit, DemandResetStatusCode code) {
        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoIds(
            rfnDeviceDao.getDeviceIdsForRfnIdentifiers(identifiers));

        Set<PaoIdentifier> devicesWithDemandResertStatusPoint =
            attributeService.getPoints(devices, BuiltInAttribute.RF_DEMAND_RESET_STATUS).keySet();
        // remove devices that do not support demand reset
        devices.removeIf(device -> !devicesWithDemandResertStatusPoint.contains(device.getPaoIdentifier()));
      
        if(limit != null) {
            devices = devices.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }
          
        for (int i = 0; i < devices.size(); i++) {
            RfnStatusArchiveRequest response = new RfnStatusArchiveRequest();
            response.setStatusPointId(i);
            DemandResetStatus status = new DemandResetStatus();
            status.setData(code);
            status.setRfnIdentifier(devices.get(i).getRfnIdentifier());
            status.setTimeStamp(new Date().getTime());
            response.setStatus(status);
            jmsTemplate.convertAndSend(JmsApiDirectory.RFN_STATUS_ARCHIVE.getQueue().getName(), response);
        }
    }
    
    @Override
    public void sendMeterInfoStatusArchiveRequest(Set<RfnIdentifier> identifiers, Instant timestamp, MeterInfo info) {
        RfnStatusArchiveRequest response = new RfnStatusArchiveRequest();
        var statusPointId = new AtomicInteger(77);
        identifiers.forEach(rfnId -> {
            var status = new MeterInfoStatus();
            status.setData(info);
            status.setTimeStamp(timestamp.getMillis());
            status.setRfnIdentifier(rfnId);
            response.setStatus(status);
            response.setStatusPointId(statusPointId.getAndIncrement());
            jmsTemplate.convertAndSend(JmsApiDirectory.RFN_STATUS_ARCHIVE.getQueue().getName(), response);
        });
    }

    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void startSimulatorWithCurrentSettings() {
        start(getSettings());
    }

    @Override
    public void setupLocations() {
      paoLocationSimulatorService.setupLocations();
    }
}
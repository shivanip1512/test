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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.cannontech.common.rfn.message.neighbor.LinkPower;
import com.cannontech.common.rfn.message.neighbor.LinkRate;
import com.cannontech.common.rfn.message.neighbor.Neighbor;
import com.cannontech.common.rfn.message.neighbor.NeighborData;
import com.cannontech.common.rfn.message.neighbor.NeighborFlag;
import com.cannontech.common.rfn.message.neighbor.Neighbors;
import com.cannontech.common.rfn.message.node.CellularIplinkRelayData;
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
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class NmNetworkSimulatorServiceImpl implements NmNetworkSimulatorService {

    private final static Logger log = YukonLogManager.getLogger(NmNetworkSimulatorServiceImpl.class);

    private static final double DISTANCE_IN_MILES = 10;
    public static final Duration incomingMessageWait = Duration.standardSeconds(1);

    private SimulatedNmMappingSettings settings;

    private volatile boolean isRunning;

    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private AttributeService attributeService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private PaoLocationSimulatorService paoLocationSimulatorService;
    @Autowired private NetworkTreeSimulatorService networkTreeSimulatorService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private YukonJmsTemplate jmsTemplate;

    private YukonJmsTemplate networkTreeUpdateRequestJmsTemplate;
    private YukonJmsTemplate networkTreeUpdateResponseJmsTemplate;
    private YukonJmsTemplate rfnMeterDemandResetJmsTemplate;
    private YukonJmsTemplate rfMetadataMultiJmsTemplate;
    private YukonJmsTemplate rfnStatusArchiveJmsTemplate;
    private ScheduledFuture<?> task;
    private Set<PaoType> wiFiSuperMeters = Set.of(PaoType.WRL420CL, PaoType.WRL420CD);
    private Set<PaoType> cellIPLinkRelays = Set.of(PaoType.CRLY856);
    private NetworkTreeUpdateTimeResponse networkTreeUpdateTimeResponse;

    private final Cache<RfnIdentifier, RfnVertex> vertexCache = CacheBuilder.newBuilder().expireAfterWrite(8, TimeUnit.HOURS)
            .build();

    @PostConstruct
    public void init() {
        networkTreeUpdateRequestJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.NETWORK_TREE_UPDATE_REQUEST, incomingMessageWait);
        networkTreeUpdateResponseJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.NETWORK_TREE_UPDATE_RESPONSE);
        rfnMeterDemandResetJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RFN_METER_DEMAND_RESET, incomingMessageWait);
        rfMetadataMultiJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RF_METADATA_MULTI, incomingMessageWait);
        rfnStatusArchiveJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RFN_STATUS_ARCHIVE);
    }

    @Override
    public void start(SimulatedNmMappingSettings settings) {
        updateSettings(settings);
        isRunning = true;
        task = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    receiveMetaDataMultiMessage();
                    receiveDemandResetMessage();
                    receiveNetworkTreeUpdateTimeMessage();
                } catch (Exception e) {
                    log.error("Error occurred in NM Network Simulator.", e);
                }
            }

            private void receiveNetworkTreeUpdateTimeMessage() throws JMSException {
                Object message = networkTreeUpdateRequestJmsTemplate.receive();
                if (message != null) {
                    ObjectMessage requestMessage = (ObjectMessage) message;
                    if (requestMessage.getObject() instanceof NetworkTreeUpdateTimeRequest) {
                        NetworkTreeUpdateTimeRequest request = (NetworkTreeUpdateTimeRequest) requestMessage.getObject();
                        log.debug("NetworkTreeUpdateTimeRequest received {}", request);

                        if (!request.isForceRefresh() && networkTreeUpdateTimeResponse != null) {
                            networkTreeUpdateResponseJmsTemplate.convertAndSend(networkTreeUpdateTimeResponse);
                            return;
                        }

                        if (request.isForceRefresh()) {
                            vertexCache.asMap().keySet().forEach(gateway -> {
                                RfnVertex vertex = networkTreeSimulatorService
                                        .buildVertex(rfnGatewayService.getGatewayByRfnIdentifier(gateway));
                                vertexCache.put(gateway, vertex);
                            });

                        }
                        networkTreeUpdateTimeResponse = new NetworkTreeUpdateTimeResponse();
                        networkTreeUpdateTimeResponse.setTreeGenerationStartTimeMillis(System.currentTimeMillis());
                        networkTreeUpdateTimeResponse.setTreeGenerationEndTimeMillis(new DateTime().plusMinutes(1).getMillis());
                        networkTreeUpdateTimeResponse
                                .setNextScheduledRefreshTimeMillis(new DateTime().plusMinutes(2).getMillis());
                        networkTreeUpdateTimeResponse
                                .setNoForceRefreshBeforeTimeMillis(new DateTime().plusMinutes(3).getMillis());
                        networkTreeUpdateResponseJmsTemplate.convertAndSend(networkTreeUpdateTimeResponse);
                    }
                }
            }

            private void receiveDemandResetMessage() throws JMSException {
                Object demandResetMessage = rfnMeterDemandResetJmsTemplate.receive();
                if (demandResetMessage != null) {
                    ObjectMessage requestMessage = (ObjectMessage) demandResetMessage;
                    if (requestMessage.getObject() instanceof RfnMeterDemandResetRequest) {
                        RfnMeterDemandResetRequest request = (RfnMeterDemandResetRequest) requestMessage.getObject();
                        log.error("RfnMeterDemandResetRequest meter identifiers {}", request.getRfnMeterIdentifiers().size());

                        RfnMeterDemandResetReply reply = new RfnMeterDemandResetReply();
                        Map<RfnIdentifier, RfnMeterDemandResetReplyType> replies = request.getRfnMeterIdentifiers().stream()
                                .collect(Collectors.toMap(Function.identity(), identifier -> RfnMeterDemandResetReplyType.OK));
                        reply.setReplyTypes(replies);
                        jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), reply);

                        String statusCode = yukonSimulatorSettingsDao
                                .getStringValue(YukonSimulatorSettingsKey.DEMAND_RESET_STATUS_ARCHIVE);

                        sendDemandResetStatusArchiveRequest(request.getRfnMeterIdentifiers(), null,
                                DemandResetStatusCode.valueOf(statusCode));
                    }
                }
            }

            private void receiveMetaDataMultiMessage() throws JMSException {
                Object metaDataMultiMessage = rfMetadataMultiJmsTemplate.receive();
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

        if (parts.isEmpty()) {
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
                    } else if (multi == RfnMetadataMulti.BATTERY_NODE_PARENT) {
                        addObjectToResult(results, device, multi, getParent(rfnDevice.getRfnIdentifier()));
                    } else if (multi == RfnMetadataMulti.NEIGHBORS) {
                        addObjectToResult(results, device, multi, getNeighbors(rfnDevice.getRfnIdentifier()));
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
        info.setSensorFirmwareVersion("R31.3.37");
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

    private RouteData getRouteData(RfnDevice rfnDevice, RfnIdentifier gateway) {
        RouteData routeData = new RouteData();
        routeData.setDestinationAddress(settings.getRouteData().getDestinationAddress());
        routeData.setHopCount((short) getRandomNumberInRange(1, 25));
        routeData.setNextHopAddress(settings.getRouteData().getNextHopAddress());
        routeData.setRouteColor(settings.getRouteData().getRouteColor());
        routeData.setRouteDataTimestamp(settings.getRouteData().getRouteDataTimestamp());
        routeData.setRouteFlags(settings.getRouteData().getRouteFlags());
        routeData.setRouteTimeout(settings.getRouteData().getRouteTimeout());
        routeData.setTotalCost(settings.getRouteData().getTotalCost());
        // for simulator next hop is always gateway
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
        neighborData.setEtxBand((short) randomElement);
        List<Integer> numSamples = Arrays.asList(0, 49, 50, 51, 52, 53, 499, 500, 501, 502);
        randomElement = numSamples.get(new Random().nextInt(numSamples.size()));
        neighborData.setNumSamples(randomElement);
        neighborData.setNeighborMacAddress(settings.getNeighborData().getNeighborMacAddress());
        neighborData.setNeighborDataTimestamp(settings.getNeighborData().getNeighborDataTimestamp());
        neighborData.setLastCommTime(settings.getNeighborData().getLastCommTime());
        neighborData.setCurrentLinkPower(settings.getNeighborData().getCurrentLinkPower());
        neighborData.setCurrentLinkRate(settings.getNeighborData().getCurrentLinkRate());
        neighborData.setNeighborFlags(settings.getNeighborData().getNeighborFlags());
        neighborData.setNextCommTime(settings.getNeighborData().getNextCommTime());
        return neighborData;
    }

    private NodeData getNodeData(RfnDevice rfnDevice) {
        NodeData node = new NodeData();
        node.setFirmwareVersion("R2.1.5Wp");
        node.setHardwareVersion("1.1.1 (Sim)");
        node.setBootLoaderVersion((long)2);
        node.setInNetworkTimestamp(1517588257267L);
        node.setMacAddress("11:22:33:44:91:11");
        node.setNetworkAddress("00C36E09081400");
        node.setNodeType(NodeType.ELECTRIC_NODE);
        node.setProductNumber("123456789 (Sim)");
        node.setNodeSerialNumber("4260060913");
        node.setSecondaryModuleFirmwareVersion("R2.2.0Wp");
        node.setNodeIpv6Address("2001:db8:3333:4444:5555:6666:7777:8888");
        if (wiFiSuperMeters.contains(rfnDevice.getPaoIdentifier().getPaoType())) {
            node.setWifiSuperMeterData(getSuperMeterData());
        }
        if (cellIPLinkRelays.contains(rfnDevice.getPaoIdentifier().getPaoType())) {
            node.setCellularIplinkRelayData(getCellIPLinkData());
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
        superMeterData.setVirtualGwIpv6Addr("FD30:0000:0000:0001:0214:08FF:FE0A:BF91");
        return superMeterData;
    }
    
    private CellularIplinkRelayData getCellIPLinkData() {
        CellularIplinkRelayData cellIPLinkData = new CellularIplinkRelayData();
        cellIPLinkData.setApn("internet.yukon");
        cellIPLinkData.setFirmwareVersion("R1.3.37");
        cellIPLinkData.setIccid("8981100022152967705");
        cellIPLinkData.setImei("358295896516576");
        cellIPLinkData.setImsi("310170845466094");
        cellIPLinkData.setModemEnabled(true);
        cellIPLinkData.setSimCardPresent(true);
        cellIPLinkData.setVirtualGwIpv6Addr("AC07:2F23:85B0:70E5:59F6:F9A7:EC14:996F");
        return cellIPLinkData;
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

            // Metadata
            simulatedNmMappingSettings.setMetadataResponseType(RfnMetadataMultiResponseType
                    .valueOf((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_METADATA_RESPONSE_TYPE.getDefaultValue()));
            simulatedNmMappingSettings.setMetadataQueryResponseType(RfnMetadataMultiQueryResultType.valueOf(
                    (String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_METADATA_DEVICE_RESPONSE_TYPE.getDefaultValue()));
            simulatedNmMappingSettings.setMetadataResponseString(
                    (String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_METADATA_RESPONSE_STRING.getDefaultValue());

            simulatedNmMappingSettings.setNeighborData(createNeighborDataFromSettings());
            simulatedNmMappingSettings.setRouteData(createRouteDataFromSettings());

            // Network Tree
            simulatedNmMappingSettings.setEmptyNullPercent(
                    yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_PERCENT_NULL));
            simulatedNmMappingSettings
                    .setMaxHop(yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_MAX_HOP));
            simulatedNmMappingSettings.setNodesOneHop(
                    yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_NODES_ONE_HOP));
            simulatedNmMappingSettings.setNumberOfDevicesPerGateway(
                    yukonSimulatorSettingsDao.getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_NUM_DEVICES_PER_GW));
            simulatedNmMappingSettings.setCreateGateways(
                    yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_CREATE_GW));

            settings = simulatedNmMappingSettings;
        }
        return settings;
    }

    private NeighborData createNeighborDataFromSettings() {
        NeighborData neighborData = new NeighborData();
        neighborData
                .setNeighborMacAddress((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_ADDR.getDefaultValue());
        neighborData
                .setEtxBand((short) ((int) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_EXT_BAND.getDefaultValue()));
        neighborData.setLastCommTime(new Date().getTime());

        neighborData.setCurrentLinkPower(
                LinkPower.valueOf((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_LINK_POW.getDefaultValue()));
        neighborData.setCurrentLinkRate(LinkRate
                .valueOf((String) (String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_LINK_RATE.getDefaultValue()));

        neighborData.setNeighborDataTimestamp(new Date().getTime());
        // flags
        Set<NeighborFlag> types = new HashSet<>();
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_PRIM_FORW_ROUTE.getDefaultValue()) {
            types.add(NeighborFlag.PRIMARY_FORWARD);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_PRIM_REV_ROUTE.getDefaultValue()) {
            types.add(NeighborFlag.PRIMARY_REVERSE);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_SEC_ALT_GATEWAY.getDefaultValue()) {
            types.add(NeighborFlag.SECONDARY_FOR_ALT_GW);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_FLOAT_NEIGHB.getDefaultValue()) {
            types.add(NeighborFlag.FLOAT);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_IGNORED_NEIGHB.getDefaultValue()) {
            types.add(NeighborFlag.IGNORED);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHB_BATTERY_NEIGHB.getDefaultValue()) {
            types.add(NeighborFlag.BATTERY);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_SEC_SERV_GATEWAY.getDefaultValue()) {
            types.add(NeighborFlag.SECONDARY_FOR_SERVING_GW);
        }
        neighborData.setNeighborFlags(types);
        neighborData.setNextCommTime(new Date().getTime());
        return neighborData;
    }

    private RouteData createRouteDataFromSettings() {
        RouteData routeData = new RouteData();
        routeData.setHopCount((short) ((int) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_HOP_COUNT.getDefaultValue()));
        routeData.setRouteColor((short) ((int) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_COLOR.getDefaultValue()));
        routeData.setRouteDataTimestamp(new Date().getTime());
        // flags
        Set<RouteFlag> routeTypes = new HashSet<>();
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_FORW_ROUTE.getDefaultValue()) {
            routeTypes.add(RouteFlag.PRIMARY_FORWARD);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_REV_ROUTE.getDefaultValue()) {
            routeTypes.add(RouteFlag.PRIMARY_REVERSE);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_BATTERY_ROUTE.getDefaultValue()) {
            routeTypes.add(RouteFlag.BATTERY);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_START_GC.getDefaultValue()) {
            routeTypes.add(RouteFlag.ROUTE_START_GC);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_REM_UPDATE.getDefaultValue()) {
            routeTypes.add(RouteFlag.ROUTE_REMEDIAL_UPDATE);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_IGNORED_ROUTE.getDefaultValue()) {
            routeTypes.add(RouteFlag.IGNORED);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_VALID_ROUTE.getDefaultValue()) {
            routeTypes.add(RouteFlag.VALID);
        }
        if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_TIMED_OUT.getDefaultValue()) {
            routeTypes.add(RouteFlag.TIMED_OUT);
        }
        routeData.setRouteFlags(routeTypes);

        routeData.setRouteTimeout(new Date().getTime());
        routeData.setTotalCost((short) ((int) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_COST.getDefaultValue()));
        return routeData;
    }

    /**
     * This method is only used for water meters
     * Water node’s parent must always be an electric node. Electric nodes are devices such as – electric meters, LCRs,
     * DA devices (CBC?), RF relays. Water meters are attached to battery nodes that is because it is not always
     * guaranteed to have an available powerline in places they want to measure water usage
     * The parent of the water meter can be LCR, CBC, RELAY, RF METER
     */
    public RfnIdentifier getParent(RfnIdentifier identifier) {
        RfnDevice device = rfnDeviceDao.getDeviceForExactIdentifier(identifier);
        // get 10 closest neighbors
        List<RfnDevice> neighbors = getNeighbors(device, 10);
        // parent is one of the neighbors that is not a gateway or a water meter
        RfnDevice parent = neighbors.stream().filter(e -> !e.getPaoIdentifier().getPaoType().isRfGateway()
                && !e.getPaoIdentifier().getPaoType().isWaterMeter()).findFirst().orElse(null);
        if (parent == null) {
            return null;
        }
        return parent.getRfnIdentifier();
    }

    /**
     * Creates a response with the neighbor information.
     */
    public Neighbors getNeighbors(RfnIdentifier identifier) {
        RfnDevice device = rfnDeviceDao.getDeviceForExactIdentifier(identifier);
        int max = getRandomNumberInRange(2, 8);
        List<RfnDevice> devices = getNeighbors(device, max);
        Neighbors neighbors = new Neighbors();
        Integer nodeNullPercent = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_TREE_PERCENT_NULL);
        devices.forEach(rfnDevice -> {
            RfnIdentifier rfnIdentifier = new Random().nextInt(100) < nodeNullPercent ? getNullIdentifier(
                    rfnDevice.getRfnIdentifier()) : rfnDevice.getRfnIdentifier();
            Neighbor neighbor = new Neighbor();
            if (new Random().nextBoolean()) {
                neighbor.setNodeSerialNumber("12345");
            }
            neighbor.setNeighborData(getNeighborData());
            neighbor.setRfnIdentifier(rfnIdentifier);
            neighbors.add(neighbor);
        });

        return neighbors;
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
        if (new Random().nextBoolean()) {
            return null;
        }
        return new RfnIdentifier("_EMPTY_", identifier.getSensorManufacturer(), identifier.getSensorModel());

    }

    private List<RfnDevice> getNeighbors(RfnDevice device, int max) {
        PaoLocation location = paoLocationDao.getLocation(device.getPaoIdentifier().getPaoId());
        List<PaoLocation> locations = paoLocationDao.getLocations(Origin.SIMULATOR);
        return getNearbyLocations(locations, location, DISTANCE_IN_MILES,
                DistanceUnit.MILES, max);
    }

    private List<RfnDevice> getNearbyLocations(List<PaoLocation> locations, PaoLocation location, double distance,
            DistanceUnit unit, int max) {

        List<PaoDistance> nearby = new ArrayList<>();
        Map<Integer, RfnDevice> devices = new HashMap<>();
        for (PaoLocation current : locations) {
            if (location.equals(current)) {
                continue;
            }
            double distanceTo = location.distanceTo(current, unit);
            if (distanceTo <= distance) {
                LiteYukonPAObject pao = databaseCache.getAllPaosMap().get(current.getPaoIdentifier().getPaoId());
                try {
                    RfnDevice device = rfnDeviceDao.getDeviceForId(pao.getYukonID());
                    if (RfnManufacturerModel.of(device.getRfnIdentifier()) != null) {
                        nearby.add(PaoDistance.of(pao, distanceTo, unit, current));
                        devices.put(device.getPaoIdentifier().getPaoId(), device);
                    }
                } catch (Exception e) {
                    // device doesn't have RFN identifier, example - template
                }
                if (nearby.size() == max) {
                    break;
                }
            }
        }

        Collections.sort(nearby, LocationService.ON_DISTANCE);

        return nearby.stream().map(d -> devices.get(d.getPao().getLiteID())).collect(Collectors.toList());
    }

    @Override
    public void sendDemandResetStatusArchiveRequest(Set<RfnIdentifier> identifiers, Integer limit, DemandResetStatusCode code) {
        List<RfnDevice> devices = rfnDeviceDao.getDevicesByPaoIds(
                rfnDeviceDao.getDeviceIdsForRfnIdentifiers(identifiers));

        Set<PaoIdentifier> devicesWithDemandResertStatusPoint = attributeService
                .getPoints(devices, BuiltInAttribute.RF_DEMAND_RESET_STATUS).keySet();
        // remove devices that do not support demand reset
        devices.removeIf(device -> !devicesWithDemandResertStatusPoint.contains(device.getPaoIdentifier()));

        if (limit != null) {
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
            rfnStatusArchiveJmsTemplate.convertAndSend(response);
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
            rfnStatusArchiveJmsTemplate.convertAndSend(response);
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
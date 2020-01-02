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
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.CollectionUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
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
import com.cannontech.common.rfn.message.metadata.CommStatusType;
import com.cannontech.common.rfn.message.metadata.RfnMetadata;
import com.cannontech.common.rfn.message.metadata.RfnMetadataReplyType;
import com.cannontech.common.rfn.message.metadata.RfnMetadataRequest;
import com.cannontech.common.rfn.message.metadata.RfnMetadataResponse;
import com.cannontech.common.rfn.message.metadatamulti.GatewayNodes;
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
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataReply;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataReplyType;
import com.cannontech.common.rfn.message.network.RfnPrimaryRouteDataRequest;
import com.cannontech.common.rfn.message.network.RouteData;
import com.cannontech.common.rfn.message.network.RouteFlagType;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeCommStatus;
import com.cannontech.common.rfn.message.node.NodeData;
import com.cannontech.common.rfn.message.node.NodeType;
import com.cannontech.common.rfn.message.node.WifiSecurityType;
import com.cannontech.common.rfn.message.node.WifiSuperMeterData;
import com.cannontech.common.rfn.message.tree.RfnVertex;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
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
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class NmNetworkSimulatorServiceImpl implements NmNetworkSimulatorService {
    private static final String requestQueue = "com.eaton.eas.yukon.networkmanager.network.data.request";
    private static final String metaDataRequestQueue = "yukon.qr.obj.common.rfn.MetadataRequest";
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
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private AttributeService attributeService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private PaoLocationSimulatorService paoLocationSimulatorService;
    @Autowired private NetworkTreeSimulatorService networkTreeSimulatorService;
    
    private JmsTemplate jmsTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> task;
    String templatePrefix;
    private Set<PaoType> wiFiSuperMeters = Set.of(PaoType.RFN420CLW, PaoType.RFN420CDW);
    
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
                        } else if (requestMessage.getObject() instanceof RfnPrimaryRouteDataRequest) {
                            RfnPrimaryRouteDataRequest request =
                                (RfnPrimaryRouteDataRequest) requestMessage.getObject();
                            RfnPrimaryRouteDataReply reply = getRoute(request.getRfnIdentifier());
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), reply);
                        }
                    }
                    Object metaDataMessage = jmsTemplate.receive(metaDataRequestQueue);
                    if (metaDataMessage != null) {
                        ObjectMessage requestMessage = (ObjectMessage) metaDataMessage;
                        if (requestMessage.getObject() instanceof RfnMetadataRequest) {
                            RfnMetadataRequest request = (RfnMetadataRequest) requestMessage.getObject();
                            RfnMetadataResponse reply = new RfnMetadataResponse();
                            reply.setRfnIdentifier(request.getRfnIdentifier());
                            reply.setReplyType(RfnMetadataReplyType.OK);
                            Map<RfnMetadata, Object> metadata = new HashMap<>();
                            boolean isReady = new Random().nextBoolean();
                            // simulating only a single piece of data
                            // TODO
                            // add other metadata properties
                            if (isReady) {
                                metadata.put(RfnMetadata.COMM_STATUS, CommStatusType.READY);
                            } else {
                                metadata.put(RfnMetadata.COMM_STATUS, CommStatusType.NOT_READY);
                            }
                            
                            metadata.put(RfnMetadata.COMM_STATUS_TIMESTAMP, 1517588257267L);
                            metadata.put(RfnMetadata.GROUPS, "Simulated Group Value");
                            metadata.put(RfnMetadata.HARDWARE_VERSION, "1.1.1 (Sim)");
                            metadata.put(RfnMetadata.IN_NETWORK_TIMESTAMP, 1517588257267L);
                            metadata.put(RfnMetadata.IPV6_ADDRESS, "1234:1234:1234:1234:1234:1234:1234:1234");
                            metadata.put(RfnMetadata.NEIGHBOR_COUNT, 2);
                            metadata.put(RfnMetadata.NODE_ADDRESS, "123456789 (Sim)");
                            metadata.put(RfnMetadata.NODE_FIRMWARE_VERSION, "Simulated Firmware Version");
                            metadata.put(RfnMetadata.NODE_NAMES, "Node (Sim)");
                            metadata.put(RfnMetadata.NODE_SERIAL_NUMBER, settings.getRouteData().getSerialNumber());
                            metadata.put(RfnMetadata.NODE_TYPE, "Nodetype (Sim)");
                            metadata.put(RfnMetadata.NUM_ASSOCIATIONS, 3);
                            String primaryGateway = "Primary Gateway (Sim)";
                            List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
                            if (!gateways.isEmpty()) {
                                RfnGateway gateway = gateways.get(0);
                                if(gateway.getData() != null) {
                                    primaryGateway = gateways.get(0).getNameWithIPAddress();
                                } else {
                                    primaryGateway =  gateways.get(0).getName();
                                }
                            }
                            metadata.put(RfnMetadata.PRIMARY_GATEWAY, primaryGateway);
                            metadata.put(RfnMetadata.PRIMARY_GATEWAY_HOP_COUNT, settings.getRouteData().getHopCount().intValue());
                            metadata.put(RfnMetadata.PRIMARY_NEIGHBOR, settings.getNeighborData().getSerialNumber());
                            metadata.put(RfnMetadata.PRIMARY_NEIGHBOR_DATA_TIMESTAMP, settings.getNeighborData().getNeighborDataTimestamp());
                            metadata.put(RfnMetadata.PRIMARY_NEIGHBOR_LINK_COST, settings.getNeighborData().getNeighborLinkCost().toString());
                            metadata.put(RfnMetadata.PRODUCT_NUMBER, "123456789 (Sim)");
                            metadata.put(RfnMetadata.SUB_MODULE_FIRMWARE_VERSION, "1.1.1 (Sim)");
                            metadata.put(RfnMetadata.HOSTNAME, "Hostname");
                            RfnDevice rfnDevice = rfnDeviceDao.getDeviceForExactIdentifier(request.getRfnIdentifier());
                            if(wiFiSuperMeters.contains(rfnDevice.getPaoIdentifier().getPaoType())) {
                                metadata.put(RfnMetadata.WIFI_SUPER_METER_DATA, getSuperMeterData());
                            }
                            reply.setMetadata(metadata);
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), reply);
                        }
                    }
                    
                    recieveMetaDataMultiMessage();
                    recieveDemandResetMessage();
                    
                } catch (Exception e) {
                    log.error("Error occurred in NM Network Simulator.", e);
                }
            }

            private void recieveDemandResetMessage() throws JMSException {
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

            private void recieveMetaDataMultiMessage() throws JMSException {
                Object metaDataMultiMessage = jmsTemplate.receive(JmsApiDirectory.RF_METADATA_MULTI.getQueue().getName());
                if (metaDataMultiMessage != null) {
                    ObjectMessage requestMessage = (ObjectMessage) metaDataMultiMessage;
                    if (requestMessage.getObject() instanceof RfnMetadataMultiRequest) {
                        RfnMetadataMultiRequest request = (RfnMetadataMultiRequest) requestMessage.getObject();
                        log.info("RfnMetadataMultiRequest identifier {} metadatas {} gateway ids {} or rfn ids {}",
                            request.getRequestID(), request.getPrimaryNodesForGatewayRfnIdentifiers().size(),
                            request.getRfnIdentifiers().size(), request.getRfnMetadatas());

                        for (RfnMetadataMultiResponse reply : getPartitionedMetadataMultiResponse(request)) {
                            log.info("RfnMetadataMultiRequest identifier: {} segment: {} response: {}",
                                      request.getRequestID(), reply.getSegmentNumber(), reply.getResponseType());
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
        List<List<RfnIdentifier>> parts = Lists.partition(Lists.newArrayList(results.keySet()), 1000);
        log.debug("--Split identifiers {} into {} parts", results.size(), parts.size());
    
        for (int i = 0; i < parts.size(); i++) {
            RfnMetadataMultiResponse response = new RfnMetadataMultiResponse(request.getRequestID(), parts.size(), i + 1);
            response.setResponseType(settings.getMetadataResponseType());
            response.setQueryResults(new HashMap<>());
            parts.get(i).forEach(identifier -> response.getQueryResults().put(identifier, results.get(identifier)));
            responses.add(response);
            log.debug("--Created response {} (of {}) query results {}", response.getSegmentNumber(),
                response.getTotalSegments(), response.getQueryResults().size());
        }
        return responses;
    }
    
    private Map<RfnIdentifier, RfnMetadataMultiQueryResult> getResults(RfnMetadataMultiRequest request) {
        List<RfnGateway> allGateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        Map<RfnIdentifier, RfnMetadataMultiQueryResult> results = new HashMap<>();
        Set<RfnIdentifier> rfnIdentifiers = new HashSet<>();
        //by gateway identifier
        if (!CollectionUtils.isEmpty(request.getPrimaryNodesForGatewayRfnIdentifiers())) {
            //remove all gateways that are not in request
            allGateways.removeIf(gateway -> !request.getPrimaryNodesForGatewayRfnIdentifiers().contains(gateway.getRfnIdentifier()));
            for (RfnIdentifier device : request.getPrimaryNodesForGatewayRfnIdentifiers()) {
                RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(device);
                // find nodes and add to the identifiers for lookup
                List<RfnIdentifier> devices = getDevicesForGateway(gateway);
                rfnIdentifiers.addAll(devices);
            }
        } else if (!CollectionUtils.isEmpty(request.getRfnIdentifiers())) {
            rfnIdentifiers.addAll(request.getRfnIdentifiers());
        }
        
        Map<RfnIdentifier, RfnGateway> devicesToGateways = new HashMap<>();

        for (RfnIdentifier device: rfnIdentifiers) {
            RfnDevice rfnDevice = rfnDeviceDao.getDeviceForExactIdentifier(device);
            for(RfnMetadataMulti multi: request.getRfnMetadatas()) {
                if (multi == RfnMetadataMulti.PRIMARY_GATEWAY_NODE_COMM) {
                    populateDeviceToGatewayMapping(allGateways, devicesToGateways);
                    RfnMetadataMultiQueryResult result = getResult(results, device, multi);
                    RfnGateway gateway = devicesToGateways.get(device);
                    if(gateway != null) {
                        result.getMetadatas().put(multi, getNodeComm(device, gateway.getRfnIdentifier()));
                    }
                } else if (multi == RfnMetadataMulti.NODE_DATA) {
                    NodeData node = new NodeData();
                    node.setFirmwareVersion("Simulated Firmware Version");
                    node.setHardwareVersion("1.1.1 (Sim)");
                    node.setInNetworkTimestamp(1517588257267L);
                    node.setMacAddress("11:22:33:44:91:11");
                    node.setNetworkAddress("00C36E09081400");
                    node.setNodeSerialNumber(settings.getRouteData().getSerialNumber());
                    node.setNodeType(NodeType.ELECTRIC_NODE);
                    node.setProductNumber("123456789 (Sim)");
                    if(wiFiSuperMeters.contains(rfnDevice.getPaoIdentifier().getPaoType())) {
                        node.setWifiSuperMeterData(getSuperMeterData());
                    }
                    RfnMetadataMultiQueryResult result = getResult(results, device, multi);
                    result.getMetadatas().put(multi, node);
                } else if (multi == RfnMetadataMulti.PRIMARY_GATEWAY_NODES) {
                    GatewayNodes nodes = new GatewayNodes();
                    RfnMetadataMultiQueryResult result = getResult(results, device, multi);
                    RfnDevice gateway = rfnDeviceDao.getDeviceForExactIdentifier(device);
                    List<RfnIdentifier> devices = getDevicesForGateway(gateway);
                    Map<RfnIdentifier, NodeComm> nodeComms = devices.stream().collect(
                        Collectors.toMap(Function.identity(), d -> getNodeComm(d, device)));
                    nodes.setGatewayRfnIdentifier(device);
                    nodes.setNodeComms(nodeComms);
                    result.getMetadatas().put(multi, nodes);
                } else if (multi == RfnMetadataMulti.PRIMARY_FORWARD_NEIGHBOR_DATA) {
                    RfnMetadataMultiQueryResult result = getResult(results, device, multi);
                    // Populate all fields
                    NeighborData neighborData = new NeighborData();
                    List<Integer> linkCost = Arrays.asList(1, 2, 3, 4, 5);
                    int randomElement = linkCost.get(new Random().nextInt(linkCost.size()));
                    neighborData.setNeighborLinkCost((float) randomElement);
                    //Generates random short between 1 and 6 for the ExtBand
                    short randomEtxBand = (short)(Math.random() * (5) + 1);
                    neighborData.setEtxBand(randomEtxBand);
                    List<Integer> numSamples = Arrays.asList(49, 50, 51);
                    randomElement = numSamples.get(new Random().nextInt(numSamples.size()));
                    neighborData.setNumSamples(randomElement);
                    result.getMetadatas().put(multi, neighborData);
                } else if (multi == RfnMetadataMulti.PRIMARY_FORWARD_TREE) {
                    RfnMetadataMultiQueryResult result = getResult(results, device, multi);
                    RfnVertex vertex = networkTreeSimulatorService.buildVertex(rfnDevice);                    
                }
            }
        }
        return results;
    }

    private void populateDeviceToGatewayMapping(List<RfnGateway> allGateways,
            Map<RfnIdentifier, RfnGateway> devicesToGateways) {
        if(devicesToGateways.isEmpty()) {
            allGateways.forEach(gateway -> {
                List<RfnDevice> devices = rfnDeviceDao.getDevicesForGateway(gateway.getId());
                devices.forEach(device -> devicesToGateways.put(device.getRfnIdentifier(), gateway));
            });
        }
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

    /**
     * Returns empty success result
     * @param device 
     * @param results 
     */
    private RfnMetadataMultiQueryResult getResult(Map<RfnIdentifier, RfnMetadataMultiQueryResult> results, RfnIdentifier device, RfnMetadataMulti multi) {
        
        if(results.containsKey(device)){
            return results.get(device);
        }
        RfnMetadataMultiQueryResult result = new RfnMetadataMultiQueryResult();
        result.setResultType(RfnMetadataMultiQueryResultType.OK);
        result.setMetadatas(new HashMap<>());
        results.put(device, result);
        return result;
    }
    
    private List<RfnIdentifier> getDevicesForGateway(RfnDevice gateway) {
        int connectedNodesWarningLimit = globalSettingDao.getInteger(GlobalSettingType.GATEWAY_CONNECTED_NODES_WARNING_THRESHOLD);
        return rfnDeviceDao.getRfnIdentifiersForGateway(gateway.getPaoIdentifier().getPaoId(), connectedNodesWarningLimit);
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
            routeData.setDestinationAddress((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_DEST_ADDR.getDefaultValue());
            routeData.setHopCount((short) ((int)YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_HOP_COUNT.getDefaultValue()));
            routeData.setNextHopAddress((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_HOP_ADDR.getDefaultValue());
            routeData.setRouteColor((short) ((int)YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_COLOR.getDefaultValue()));
            routeData.setRouteDataTimestamp(new Date().getTime());
            //flags
            Set<RouteFlagType> routeTypes = new HashSet<>();
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_FORW_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlagType.PF);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_REV_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlagType.PR);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_BATTERY_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlagType.BR);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_START_GC.getDefaultValue()) {    
                routeTypes.add(RouteFlagType.GC);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_REM_UPDATE.getDefaultValue()) {    
                routeTypes.add(RouteFlagType.RU);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_IGNORED_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlagType.IR);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_VALID_ROUTE.getDefaultValue()) {    
                routeTypes.add(RouteFlagType.VR);
            }
            if ((boolean) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIM_TIMED_OUT.getDefaultValue()) {    
                routeTypes.add(RouteFlagType.TO);
            }
            routeData.setRouteFlags(routeTypes);
            
            routeData.setRouteTimeout(new Date().getTime());
            routeData.setSerialNumber("101");
            routeData.setTotalCost((short) ((int)YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_ROUTE_COST.getDefaultValue()));
            simulatedNmMappingSettings.setRouteData(routeData);
    
            //ParentData
            ParentData parentData = new ParentData();
            parentData.setNodeMacAddress((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PARENT_MAC_ADDR.getDefaultValue());
            parentData.setNodeSN((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PARENT_SN.getDefaultValue());
            simulatedNmMappingSettings.setParentData(parentData);
            
            simulatedNmMappingSettings.setNeighborReplyType(RfnNeighborDataReplyType.valueOf((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_NEIGHBOR_DATA_REPLY_TYPE.getDefaultValue()));
            simulatedNmMappingSettings.setRouteReplyType(RfnPrimaryRouteDataReplyType.valueOf((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PRIMARY_DATA_REPLY_TYPE.getDefaultValue()));
            simulatedNmMappingSettings.setParentReplyType(RfnParentReplyType.valueOf((String) YukonSimulatorSettingsKey.RFN_NETWORK_SIMULATOR_PARENT_REPLY_TYPE.getDefaultValue()));
            
            settings = simulatedNmMappingSettings;
        }
        return settings;
    }
    
    /**
     * This method is only used for water meters
     * Water node’s parent must always be an electric node. Electric nodes are devices such as – electric meters, LCRs,
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
    public RfnPrimaryRouteDataReply getRoute(RfnIdentifier identifier) {
        RfnDevice device= rfnDeviceDao.getDeviceForExactIdentifier(identifier);
        int max = getRandomNumberInRange(2, 3);
        List<RfnDevice> neighbors = getNeighbors(device, max);
        List<RouteData> routeData = new ArrayList<>();
        //add original device
        routeData.addAll(getRouteDataFromSettings(Lists.newArrayList(device)));
        //add neighbors
        routeData.addAll(getRouteDataFromSettings(neighbors));
        //add gateway
        RfnIdentifier gateway = getNearbyGateway(paoLocationDao.getLocation(device.getPaoIdentifier().getPaoId()));
        //The last RouteData object in the List<RouteData> routeData will be have all null fields except for - rfnIdentifier, and serialNumber (gateway)
        if (gateway != null) {
            RouteData gatewayData = new RouteData();
            gatewayData.setSerialNumber(gateway.getSensorSerialNumber());
            gatewayData.setRfnIdentifier(gateway);
            if (gatewayData.getRfnIdentifier().isNotBlank()) {
                routeData.add(gatewayData);
            }
        }
        RfnPrimaryRouteDataReply reply = new RfnPrimaryRouteDataReply();
        reply.setReplyType(settings.getRouteReplyType());
        reply.setRfnIdentifier(device.getRfnIdentifier());
        reply.setRouteData(routeData);
        log.info("identifier="+ identifier+" routeData="+routeData);
 
        return reply;
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

    private  List<RouteData> getRouteDataFromSettings(List<RfnDevice> neighbors){
        List<RouteData> routeData = new ArrayList<>();
        for (RfnDevice device : neighbors) {
            RouteData data = new RouteData();
            data.setDestinationAddress(settings.getRouteData().getDestinationAddress());
            data.setHopCount(settings.getRouteData().getHopCount());
            data.setRfnIdentifier(device.getRfnIdentifier());
            data.setSerialNumber(device.getRfnIdentifier().getSensorSerialNumber());
            data.setNextHopAddress(settings.getRouteData().getNextHopAddress());
            data.setRouteColor(settings.getRouteData().getRouteColor());
            data.setRouteDataTimestamp(settings.getRouteData().getRouteDataTimestamp());
            data.setRouteFlags(settings.getRouteData().getRouteFlags());
            data.setRouteTimeout(settings.getRouteData().getRouteTimeout());
            data.setTotalCost(settings.getRouteData().getTotalCost());
            routeData.add(data);
        }
        return routeData;
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
    
    private RfnIdentifier getNearbyGateway(PaoLocation location){
        List<RfnDevice> gateways = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfGatewayTypes());
        List<PaoLocation> locations = paoLocationDao.getLocations(gateways).stream().filter( x-> x.getOrigin() == Origin.SIMULATOR).collect(
            Collectors.toList());
        if(locations.isEmpty()){
            return null;
        }
        List<RfnDevice> devices = getNearbyLocations(locations, location, DISTANCE_IN_MILES, DistanceUnit.MILES, 1);
        if (devices.isEmpty()) {
            return gateways.get(0).getRfnIdentifier();
        }else{
            return devices.get(0).getRfnIdentifier();
        }
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
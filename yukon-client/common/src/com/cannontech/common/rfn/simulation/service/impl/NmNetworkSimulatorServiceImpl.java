package com.cannontech.common.rfn.simulation.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
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
import com.cannontech.common.rfn.message.network.NeighborData;
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
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.simulation.SimulatedNmMappingSettings;
import com.cannontech.common.rfn.simulation.service.NmNetworkSimulatorService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NmNetworkSimulatorServiceImpl implements NmNetworkSimulatorService {
    private static final String requestQueue = "com.eaton.eas.yukon.networkmanager.network.data.request";
    private static final String metaDataRequestQueue = "yukon.qr.obj.common.rfn.MetadataRequest";
    private static final int incomingMessageWaitMillis = 1000;
    
    private final static Logger log = YukonLogManager.getLogger(NmNetworkSimulatorServiceImpl.class);
    
    private static final int MAX_RADIUS_IN_METERS = 25000;
    private static final int MAX_DEVICE_COUNT = 250000;
    // office
    private static final double latitude = 44.985565;
    private static final double longitude = -93.404157;
    
    private static final double DISTANCE_IN_MILES = 10;
                
    private SimulatedNmMappingSettings settings;
    
    private volatile boolean isRunning;
        
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private LocationService locationService;
    @Autowired private IDatabaseCache cache;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    private JmsTemplate jmsTemplate;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> task;

    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(incomingMessageWaitMillis);
    }
    
    @Override
    public void setupLocations() {
        
        log.info("Setting up locations");
        paoLocationDao.delete(Origin.SIMULATOR);

        Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex( paoLocationDao.getAllLocations(), c -> c.getPaoIdentifier());
        List<LiteYukonPAObject> devices = cache.getAllDevices().stream().filter(
            x -> x.getPaoType().isRfn() && !locations.containsKey(x.getPaoIdentifier())).collect(Collectors.toList());
        List<PaoLocation> newLocations = new ArrayList<>();
        int estimatedRadius = getEstimatedRadius(devices.size());
        for(LiteYukonPAObject device:devices){
            newLocations.add(getLocation(device.getPaoIdentifier(), latitude, longitude, estimatedRadius));
        }
        log.info("Inserting "+newLocations.size()+" locations.");
        paoLocationDao.save(newLocations);
        log.info("Inserting "+newLocations.size()+" locations is done.");
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
                            metadata.put(RfnMetadata.PRIMARY_GATEWAY, "Primary Gateway (Sim)");
                            metadata.put(RfnMetadata.PRIMARY_GATEWAY_HOP_COUNT, settings.getRouteData().getHopCount().intValue());
                            metadata.put(RfnMetadata.PRIMARY_NEIGHBOR, settings.getNeighborData().getSerialNumber());
                            metadata.put(RfnMetadata.PRIMARY_NEIGHBOR_DATA_TIMESTAMP, settings.getNeighborData().getNeighborDataTimestamp());
                            metadata.put(RfnMetadata.PRIMARY_NEIGHBOR_LINK_COST, settings.getNeighborData().getNeighborLinkCost().toString());
                            metadata.put(RfnMetadata.PRODUCT_NUMBER, "123456789 (Sim)");
                            metadata.put(RfnMetadata.SUB_MODULE_FIRMWARE_VERSION, "1.1.1 (Sim)");
                            reply.setMetadata(metadata);
                            jmsTemplate.convertAndSend(requestMessage.getJMSReplyTo(), reply);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error occurred in NM Network Simulator.", e);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        log.info("Started NM Network Simulator");
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
            
            //NeighborData
            NeighborData neighborData = new NeighborData();
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
    
    private int getEstimatedRadius(int deviceCount){
        if(deviceCount >= MAX_DEVICE_COUNT){
            return MAX_RADIUS_IN_METERS;
        }
        return deviceCount * MAX_RADIUS_IN_METERS / MAX_DEVICE_COUNT;
    }
    
    /**
     * Creates a random location within a radius
     */
    private PaoLocation getLocation(PaoIdentifier paoIdentifier, double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double latitude = new_x + x0;
        double longitude = y + y0;

        PaoLocation location = new PaoLocation(paoIdentifier, latitude, longitude, Origin.SIMULATOR, new Instant());
        return location;
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
    public RfnPrimaryRouteDataReply getRoute(RfnIdentifier identifier) {
        RfnDevice device= rfnDeviceDao.getDeviceForExactIdentifier(identifier);
        int max = getRandomNumberInRange(2, 8);
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
        int max = getRandomNumberInRange(2, 10);
        List<RfnDevice> neighbors = getNeighbors(device, max);

        RfnNeighborDataReply reply = new RfnNeighborDataReply();
        Set<NeighborData> neighborData = getNeighborDataFromSettings(neighbors);
        
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
    
    private  Set<NeighborData> getNeighborDataFromSettings(List<RfnDevice> neighbors){
        Set<NeighborData> neighborData = new HashSet<>();
        for (RfnDevice device : neighbors) {
            NeighborData data = new  NeighborData();
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
        List<PaoDistance> distances = locationService.getNearbyLocations(locations, location, DISTANCE_IN_MILES,
            DistanceUnit.MILES, null);
        log.debug("device="+device+" distances="+distances.size()+" max="+max);
        List<List<PaoDistance>> parts = Lists.partition(distances, distances.size()/max);
        log.debug("parts="+parts.size());
        List<PaoDistance> randomDistances = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            List<PaoDistance> part = parts.get(i);
            log.debug("part size=" + part.size());
            PaoDistance lastElement = part.stream().reduce((a, b) -> b).get();
            randomDistances.add(lastElement);
        }
        List<Integer> paoIds =
            randomDistances.stream().map(x -> x.getPaoIdentifier().getPaoId()).collect(Collectors.toList());
        log.debug("neighbors found=" + paoIds.size());
        List<RfnDevice> rfDevices = rfnDeviceDao.getDevicesByPaoIds(paoIds);
        ListIterator<RfnDevice> it = rfDevices.listIterator();
        while (it.hasNext()) {
            RfnDevice d = it.next();
            if (!d.getRfnIdentifier().isNotBlank()) {
                log.debug(d + " has a blank identifier "+d.getRfnIdentifier()+"- removing");
                it.remove();
            }
        }
        return rfDevices;
    }
    
    private RfnIdentifier getNearbyGateway(PaoLocation location){

        List<RfnDevice> gateways = rfnDeviceDao.getDevicesByPaoTypes(PaoType.getRfGatewayTypes());
        List<PaoLocation> locations = paoLocationDao.getLocations(gateways).stream().filter( x-> x.getOrigin() == Origin.SIMULATOR).collect(
            Collectors.toList());
        if(locations.isEmpty()){
            return null;
        }
        List<PaoDistance> distance =
            locationService.getNearbyLocations(locations, location, DISTANCE_IN_MILES, DistanceUnit.MILES, null);
        int deviceId;
        if (distance.isEmpty()) {
            deviceId = locations.get(0).getPaoIdentifier().getPaoId();
        }else{
            deviceId = distance.get(0).getPaoIdentifier().getPaoId();
        }
        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        return device.getRfnIdentifier();
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
}

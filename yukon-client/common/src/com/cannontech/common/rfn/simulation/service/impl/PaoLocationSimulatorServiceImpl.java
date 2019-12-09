package com.cannontech.common.rfn.simulation.service.impl;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.rfn.message.node.NodeComm;
import com.cannontech.common.rfn.message.node.NodeCommStatus;
import com.cannontech.common.rfn.message.node.RfnNodeCommArchiveRequest;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.rfn.simulation.service.PaoLocationSimulatorService;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.math.IntMath;

public class PaoLocationSimulatorServiceImpl implements PaoLocationSimulatorService {
    
    private final static Logger log = YukonLogManager.getLogger(PaoLocationSimulatorServiceImpl.class);
    
    private static final int MAX_RADIUS_IN_METERS = 50000;
    private static final int MAX_DEVICE_COUNT = 250000;
    // office
    private static final double latitude = 44.985565;
    private static final double longitude = -93.404157;
    
    private static class CubFoods {
        private double latitude;
        private double longitude;
        public CubFoods(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
    private static List<CubFoods> gatewayLocations = new ArrayList<>();
    {
        gatewayLocations.add(new CubFoods(45.021110, -93.479910));
        gatewayLocations.add(new CubFoods(45.124490, -93.450420));
        gatewayLocations.add(new CubFoods(45.035020, -93.409390));
        gatewayLocations.add(new CubFoods(45.058660, -93.322500));
        gatewayLocations.add(new CubFoods(44.985930, -93.445440));
        gatewayLocations.add(new CubFoods(45.019370, -93.360530));
        gatewayLocations.add(new CubFoods(44.993120, -93.150460));
        gatewayLocations.add(new CubFoods(44.915640, -93.501860));
        gatewayLocations.add(new CubFoods(44.951510, -93.235660));
        gatewayLocations.add(new CubFoods(44.893320, -93.581530));
    }
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private ConfigurationSource configurationSource;
    private Executor executor = Executors.newScheduledThreadPool(1);
    
    private JmsTemplate jmsTemplate;
    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(NmNetworkSimulatorServiceImpl.incomingMessageWaitMillis);
    }
    
    
    @Override
    public void setupLocations() {
        String templatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");
        log.info("Setting up locations");
        paoLocationDao.delete(Origin.SIMULATOR);
        rfnDeviceDao.clearDynamicRfnDeviceData();
            
        executor.execute(() -> {
            Map<PaoIdentifier, PaoLocation> locations =
                    Maps.uniqueIndex(paoLocationDao.getAllLocations(), c -> c.getPaoIdentifier());
            
            log.info("Existing locations that are not simulated:" + locations.size());
            
            List<LiteYukonPAObject> devices = cache.getAllDevices().stream()
                    .filter(device -> ! device.getPaoName().contains(templatePrefix) && !locations.containsKey(device.getPaoIdentifier()) && !device.getPaoType().isRfGateway())
                    .collect(Collectors.toList());
            
            log.info("Device to simulate:" + devices.size());
            
            List<LiteYukonPAObject> rfnDevices = devices.stream().filter(device -> device.getPaoType()
                .isRfn()).collect(Collectors.toList());
            
            log.info("RF devices:" + rfnDevices.size());
            
            List<LiteYukonPAObject> nonRfnDevices = devices.stream().filter(device -> !device.getPaoType()
                .isRfn()).collect(Collectors.toList());
            
            log.info("NON RF devices:" + nonRfnDevices.size());
            
            int estimatedRadius = getEstimatedRadius(nonRfnDevices.size());
            // add locations for devices that are not RF
            List<PaoLocation> newLocations = nonRfnDevices.stream()
                    .map(device -> getLocation(device.getPaoIdentifier(), latitude, longitude, estimatedRadius))
                    .collect(Collectors.toList());
                    
            log.info("Locations for NON RF devices:" + newLocations.size());
            
            createRfnIdentifiers(rfnDevices); 
            
            //Limit gateways in Yukon to 10 that will be mapped to devices. We will map those gateways to 10 Cub Foods.
            List<RfnGateway> gateways = rfnGatewayService.getAllGateways().stream()
                    .filter(gateway -> !locations.containsKey(gateway.getPaoIdentifier()))
                    .limit(gatewayLocations.size())
                    .collect(Collectors.toList());
            
            log.info("Gateways:" + gateways.size());
            //split devices between gateways
            List<List<LiteYukonPAObject>> rfnDevicesSplit = partition(rfnDevices, gateways.size());
            
            log.info("Partitioned devices into parts:" + rfnDevicesSplit.size());
            
            RfnNodeCommArchiveRequest request = new RfnNodeCommArchiveRequest();
            request.setNodeComms(new HashMap<Long, NodeComm>());
            AtomicLong ackId = new AtomicLong(1);
            for(int i = 0; i < gateways.size(); i++) {
                RfnGateway gateway = gateways.get(i);
                CubFoods cub = gatewayLocations.get(i);                
                List<LiteYukonPAObject> devicesForGateway = rfnDevicesSplit.get(i);
                
                log.info("Mapping gateway:" + gateway + " to Cub Foods:" + i + " devices:" + devicesForGateway.size());
                
                //radius around CubFoods
                int radius = getEstimatedRadius(devicesForGateway.size());
                //add gateway location
                newLocations.add(new PaoLocation(gateway.getPaoIdentifier(), cub.latitude, cub.longitude,
                    Origin.SIMULATOR, new Instant()));
                //calculate device location within a certain radius around Cub Foods
                newLocations.addAll(devicesForGateway.stream()
                    .map(device -> getLocation(device.getPaoIdentifier(), cub.latitude, cub.longitude, radius))
                    .collect(Collectors.toList()));
                //map gateway to device
                List<Integer> devicesIds = devicesForGateway.stream()
                    .map(device -> device.getLiteID()).collect(Collectors.toList());
                rfnDeviceDao.getDevicesByPaoIds(devicesIds).forEach(device ->{
                    NodeComm nodeComm = new NodeComm();
                    nodeComm.setDeviceRfnIdentifier(device.getRfnIdentifier());
                    nodeComm.setGatewayRfnIdentifier(gateway.getRfnIdentifier());
                    nodeComm.setNodeCommStatus(NodeCommStatus.READY);
                    nodeComm.setNodeCommStatusTimestamp(System.currentTimeMillis());
                    request.getNodeComms().put(ackId.getAndIncrement(), nodeComm);
                });
            }
            jmsTemplate.convertAndSend(JmsApiDirectory.RFN_NODE_COMM_ARCHIVE.getQueue().getName(), request);
            log.info("Inserting " + newLocations.size() + " locations.");
            paoLocationDao.save(newLocations);
            log.info("Inserting " + newLocations.size() + " locations is done.");
        });
    }
    
    private List<List<LiteYukonPAObject>> partition(List<LiteYukonPAObject> allDevices, int divisor) {
        int chunk = IntMath.divide(allDevices.size(), divisor, RoundingMode.CEILING);
        return Lists.partition(allDevices, chunk);
    }
    
    //If RFN identifiers do not exist creates identifiers
    private void createRfnIdentifiers(List<LiteYukonPAObject> devices) {
        List<LiteYukonPAObject> rfnDevices =
            devices.stream().filter(d -> d.getPaoType().isRfn()).collect(Collectors.toList());
        Map<Integer, RfnDevice> rfnDevicesWithIdentifiers = rfnDeviceDao.getDevicesByPaoIds(
            rfnDevices.stream().map(d -> d.getLiteID()).collect(Collectors.toList())).stream().collect(
                Collectors.toMap(d -> d.getPaoIdentifier().getPaoId(), Function.identity()));
        List<LiteYukonPAObject> devicesWithoutIdentifiers =
            rfnDevices.stream().filter(d -> !rfnDevicesWithIdentifiers.containsKey(d.getLiteID())).collect(
                Collectors.toList());
        devicesWithoutIdentifiers.forEach(d -> {
            RfnManufacturerModel template = RfnManufacturerModel.getForType(d.getPaoType()).get(0);
            RfnIdentifier rfId = new RfnIdentifier(String.valueOf(d.getLiteID()), template.getManufacturer(), template.getModel());
            try {
                rfnDeviceDao.updateDevice(new RfnDevice(d.getPaoName(), d, rfId));
            }catch(Exception e) {
                log.error("Unable to update device {} with {}", d, rfId);
            }
        });
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
}

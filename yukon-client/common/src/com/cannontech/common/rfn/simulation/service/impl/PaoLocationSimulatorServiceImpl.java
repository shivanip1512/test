package com.cannontech.common.rfn.simulation.service.impl;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.rfn.message.tree.NetworkTreeUpdateTimeRequest;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.rfn.service.RfnDeviceCreationService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.rfn.simulation.service.PaoLocationSimulatorService;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.math.IntMath;

public class PaoLocationSimulatorServiceImpl implements PaoLocationSimulatorService {
    
    private final static Logger log = YukonLogManager.getLogger(PaoLocationSimulatorServiceImpl.class);
    
   
    public static class Starbucks {
        public double longitude;
        public double latitude;
        public Starbucks(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
        @Override
        public String toString() {
            return longitude+" "+latitude+"\n";
        }
    }
    
   private static String simulatedGateway = "Simulated Gateway";    
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private RfnDeviceCreationService rfnDeviceCreationService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate jmsTemplate;

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.NETWORK_TREE_UPDATE_REQUEST);
    }

    @Override
    public void setupLocations() {
        String templatePrefix = configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");
        log.info("Setting up locations");
        log.info("Deleteing locations created by simulator");
        paoLocationDao.delete(Origin.SIMULATOR);
        log.info("Deleting gateway to device mappings");
        rfnDeviceDao.clearDynamicRfnDeviceData();

        List<Starbucks> starbucksLocations = SimulatedLocationParser.parseStarbucksLocations();
        log.info("Parsed starbucks locations {}", starbucksLocations);
        Map<PaoIdentifier, PaoLocation> locations = Maps.uniqueIndex(paoLocationDao.getAllLocations(), c -> c.getPaoIdentifier());

        log.info("{} not sumulated locations", locations.size());

        List<LiteYukonPAObject> devices = cache.getAllDevices().stream()
                .filter(device -> !device.getPaoName().contains(templatePrefix)
                        && !locations.containsKey(device.getPaoIdentifier()) && !device.getPaoType().isRfGateway())
                .collect(Collectors.toList());

        List<LiteYukonPAObject> allRfnDevices = devices.stream().filter(device -> device.getPaoType()
                .isRfn()).collect(Collectors.toList());

        List<LiteYukonPAObject> nonRfnDevices = devices.stream().filter(device -> !device.getPaoType()
                .isRfn()).collect(Collectors.toList());

        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        log.info("{} gateways {} devices rfn {} none rfn {}", gateways.size(), devices.size(), allRfnDevices.size(),
                nonRfnDevices.size());

        // If RFN identifiers do not exist creates identifiers
        createRfnIdentifiers(allRfnDevices);

        int numberOfDevicesPerGateway = yukonSimulatorSettingsDao
                .getIntegerValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_NUM_DEVICES_PER_GW);
        
        // split devices between gateways
        List<List<LiteYukonPAObject>> rfnDevicesSplit = Lists.partition(allRfnDevices, numberOfDevicesPerGateway);
        // we are going to add none rfn devices around the gateways as well
        List<List<LiteYukonPAObject>> noneRfnDevicesSplit = partitionByChunks(nonRfnDevices, rfnDevicesSplit.size());

        log.info("Partitioned device chunks: rfn devices {} none rfn devices {}", rfnDevicesSplit.size(),
                noneRfnDevicesSplit.size());

        gateways = createAdditionalGateways(gateways, rfnDevicesSplit);

        int starbucksCounter = 0;
        int deviceChunkCounter = 0;

        // radius around starbucks
        int radius = 25000;

        List<PaoLocation> newLocations = new ArrayList<>();

        Instant now = Instant.now();
        Set<DynamicRfnDeviceData> datas = new HashSet<>();
        for (RfnGateway gateway : gateways) {
            List<LiteYukonPAObject> rfnDevices = rfnDevicesSplit.get(deviceChunkCounter);
            if (rfnDevices == null) {
                break;
            }
            PaoLocation gatewayLocation = locations.get(gateway.getPaoIdentifier());
            if (gatewayLocation == null) {
                Starbucks location = starbucksLocations.get(starbucksCounter++);
                log.info("mapping {} to the starbucks #{} {}" , gateway.getName(), starbucksCounter, location);
                gatewayLocation = new PaoLocation(gateway.getPaoIdentifier(), location.latitude, location.longitude,
                        Origin.SIMULATOR, new Instant());
                newLocations.add(gatewayLocation);
            }
            addDeviceLocations(radius, newLocations, gatewayLocation, rfnDevices);
            List<LiteYukonPAObject> noneRfnDevices = noneRfnDevicesSplit.get(deviceChunkCounter);
            if (noneRfnDevices != null) {
                log.info("creating  location for none rfn devices {}", noneRfnDevices.size());
                addDeviceLocations(radius, newLocations, gatewayLocation, noneRfnDevices);
            }
            addDeviceToGatewayMapping(gateway, rfnDevices, datas, now);
            deviceChunkCounter++;
        }
        rfnDeviceDao.saveDynamicRfnDeviceData(datas);
        log.info("Inserting {} locations.", newLocations.size());
        paoLocationDao.save(newLocations);
        log.info("Inserting {} locations is done.", newLocations.size());
        NetworkTreeUpdateTimeRequest request = new NetworkTreeUpdateTimeRequest();
        request.setForceRefresh(true);
        log.debug("Sending NetworkTreeUpdateTimeRequest message to request reload of network tree information.");
        jmsTemplate.convertAndSend(request);
    }

    private Set<RfnGateway> createAdditionalGateways(Set<RfnGateway> gateways, List<List<LiteYukonPAObject>> rfnDevicesSplit) {
        boolean createGateways = yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.RFN_NETWORK_SIM_CREATE_GW);
        if(!createGateways) {
            return gateways;
        }
        int newGatewaysToCreate = rfnDevicesSplit.size() - gateways.size();
        if (newGatewaysToCreate > 0) {
            createNewGateways(gateways, newGatewaysToCreate);
            gateways = rfnGatewayService.getAllGateways();
        }
        return gateways;
    }

    private void addDeviceToGatewayMapping(RfnGateway gateway,
            List<LiteYukonPAObject> devicesForGateway, Set<DynamicRfnDeviceData> datas, Instant now) {
        List<Integer> deviceIds = devicesForGateway.stream().map(device -> device.getLiteID()).collect(Collectors.toList());
        List<RfnDevice> rfnDevices = rfnDeviceDao.getDevicesByPaoIds(deviceIds);
        rfnDevices.forEach(device -> {
            DynamicRfnDeviceData data = new DynamicRfnDeviceData(device, gateway, getRandomNumberInRange(1, 130), now);
            datas.add(data);
        });
    }
    
    private int getRandomNumberInRange(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    /**
     * Calculated device location within a certain radius
     */
    private void addDeviceLocations(int radius, List<PaoLocation> newLocations, PaoLocation gatewayLocation,
            List<LiteYukonPAObject> devicesForGateway) {
        newLocations.addAll(devicesForGateway.stream()
            .map(device -> getLocation(device.getPaoIdentifier(), gatewayLocation.getLatitude(), gatewayLocation.getLongitude(), radius))
            .collect(Collectors.toList()));
    }
    
    private void createNewGateways(Set<RfnGateway> existingGateways, int numberOfNewGateways) {
        AtomicInteger counter = new AtomicInteger(0);
        log.info("Creating {} gateways", numberOfNewGateways);
        while (numberOfNewGateways != 0) {
            int i = counter.incrementAndGet();
            if (existingGateways.stream()
                    .filter(gateway -> gateway.getName().equals(simulatedGateway + " " + i))
                    .findFirst().isPresent()) {
                continue;
            }
            RfnIdentifier identifier = new RfnIdentifier(Integer.toString(i), "EATON", "RFGateway");
            String name = simulatedGateway + " " + i;
            log.info("Creating {} {}", name, identifier);
            rfnDeviceCreationService.createGateway(name, identifier);
            numberOfNewGateways--;
        }
    }

    private List<List<LiteYukonPAObject>> partitionByChunks(List<LiteYukonPAObject> allDevices, int divisor) {
        int chunk = IntMath.divide(allDevices.size(), divisor, RoundingMode.CEILING);
        return Lists.partition(allDevices, chunk);
    }
    
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

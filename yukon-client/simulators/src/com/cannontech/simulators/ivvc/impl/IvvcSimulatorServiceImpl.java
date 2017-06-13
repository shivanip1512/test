package com.cannontech.simulators.ivvc.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.capcontrol.model.RegulatorEvent.EventType;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.Phase;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.simulators.dao.RegulatorEventsSimulatorDao;
import com.cannontech.simulators.dao.RegulatorEventsSimulatorDao.RegulatorOperations;
import com.cannontech.simulators.ivvc.IvvcSimulatorService;
import com.google.common.base.Optional;

/**
 * 
 */
public class IvvcSimulatorServiceImpl implements IvvcSimulatorService {
    private static final Logger log = YukonLogManager.getLogger(IvvcSimulatorServiceImpl.class);
    
    @Autowired private CapControlCache capControlCache;
    @Autowired private ZoneService zoneService;
    @Autowired private VoltageRegulatorService regulatorService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private RegulatorEventsSimulatorDao regulatorEventsSimulatorDao;
    @Autowired @Qualifier("main") private ScheduledExecutor executor;

    private volatile boolean isRunning; // This refers to the simulator itself
    private volatile boolean isCurrentlyExecuting = false; // This is used to ensure only one timed execution happens at a time
    private boolean tapPositionsPreloaded = false;
    
    private Map<Integer, Integer> regulatorTapPositions = new HashMap<>();
    private int keepAliveIncrementingValue = 0;
    Instant lastRegulatorEvaluationTime;
    ScheduledFuture<?> future;
    
    private final int END_OF_LINE = 40000;
    private final int INITIAL_TAP_POSITION = 3;
    
    @Override
    public boolean start() {
        if (isRunning) {
            return false;
        } else {
            lastRegulatorEvaluationTime = Instant.now();
            future = executor.scheduleAtFixedRate(this::getSimulatorThread, 0, 30, TimeUnit.SECONDS);
            log.info("IVVC simulator thread starting up.");
            isRunning = true;
            return true;
        }
    }
    
    @Override
    public void stop() {
        future.cancel(true);
        isRunning = false;
        log.info("Cap Control simulator thread shutting down.");
    }
    
    @Override
    public boolean isRunning() {
        return isRunning;
    }
    
    private void getSimulatorThread() {
        if (!isCurrentlyExecuting) {
            isCurrentlyExecuting = true;
            try {
                loadRegulatorTapPositionsFromDatabase();
                processNewRegulatorActions(lastRegulatorEvaluationTime);
                lastRegulatorEvaluationTime = Instant.now();
                calculateAndSendCapControlPoints();

            } catch (Exception e) {
                log.error("Error occurred in IVVC simulator.", e);
            }
        } else {
            log.error("Unable to run IVVC Simulator, the thread is probably taking more than 30 seconds to run");
        }
        isCurrentlyExecuting = false;
    }
    
    // Load all of the tap up and down operations, add or subtract from tap position depending on operation performed.
    // TBD: Limits position?
    private void processNewRegulatorActions(Instant lastRegulatorEvaluationTime) {
        List<RegulatorOperations> regulatorTapOperations = regulatorEventsSimulatorDao.getRegulatorTapOperationsAfter(lastRegulatorEvaluationTime);
        
        if (!regulatorTapOperations.isEmpty()) {
            log.debug("Found " + regulatorTapOperations.size() + " tap operations, acting on them.");
        }
        
        for (RegulatorOperations regulatorOperations : regulatorTapOperations) {
            Integer tapPosition = Optional.fromNullable(regulatorTapPositions.get(regulatorOperations.regulatorId)).or(INITIAL_TAP_POSITION);
            
            if (regulatorOperations.eventType.equals(EventType.TAP_UP)) {
                regulatorTapPositions.put(regulatorOperations.regulatorId, tapPosition + 1);
            } else if (regulatorOperations.eventType.equals(EventType.TAP_DOWN)) {
                regulatorTapPositions.put(regulatorOperations.regulatorId, tapPosition - 1);
            }
        }
    }
    
    private void calculateAndSendCapControlPoints() {
        List<Area> simulatedAreas = capControlCache.getAreas().stream()
                                                        .filter(area->area.getCcName().startsWith("Sim Area"))
                                                        .collect(Collectors.toList());
        
        // Each Sub Bus has its own kw and kVar that are what the system would have with no banks or regulators
        // The actual kVar shown on the UI is that base kVar minus the currently active bank kVar
        // Currently we do no additional math on the incoming kW
        // The current setup has 4500 kVar/Feeder *2 feeders = 9k kVar so our current target kVar is -750 to 9750
        // The minimum output of our system will be around 3 kW and -750 kVar
        // We use kVar to drive the kW to make future changes to banks and desired KVar easier
        final double KW_TO_KVAR_MULTIPLIER = .75;
        final int MAX_KVAR = 9750; // TODO calculate this from the database
        final int MIN_KVAR = -750;
        final int MIN_KW = 3000;
        final int MAX_KW = (int) (MIN_KW + (MAX_KVAR - MIN_KVAR)/KW_TO_KVAR_MULTIPLIER);
        
        int secondsOfDayForCalculation = DateTime.now().getSecondOfDay()+12*60*60; // This shifts the sine so the peak is at 18:00
        final double currentSubBusBaseKw = MIN_KW + (Math.sin(secondsOfDayForCalculation*2*Math.PI/DateTimeConstants.SECONDS_PER_DAY)*((MAX_KW-MIN_KW)/2) + ((MAX_KW-MIN_KW)/2));
        final double currentSubBusBaseKVar = (currentSubBusBaseKw - MIN_KW)*KW_TO_KVAR_MULTIPLIER + MIN_KVAR; //When we are at minimum kw, we are at MIN_KVAR
        keepAliveIncrementingValue = (keepAliveIncrementingValue + 1) & 0xFFFF; // Rolls over after 16 bits.
        
        for (Area area : simulatedAreas) {
            List<SubBus> subBusesByArea = capControlCache.getSubBusesByArea(area.getCcId());
            for (SubBus subBus : subBusesByArea) {
                int subBusKVarClosed = 0;
                Map<Integer, Integer> capBankToFeeder = new HashMap<>();
                List<Feeder> feedersBySubBus = capControlCache.getFeedersBySubBus(subBus.getCcId());
                // Feeder voltage rises affect every device on the feeder and need a bunch of data to calculate. 
                // We gather them here until we finally have the right value and use it everywhere.
                Map<Integer, Double> feederVoltageRises = feedersBySubBus.stream()
                                                                         .collect(Collectors.toMap(feeder->feeder.getCcId(), 
                                                                                                   feeder->0.0));
                                                                
                for (Feeder feeder : feedersBySubBus) {
                    int feederActiveBankKVar = 0;
                    
                    // Record the feeder kVar shift and Voltage shift from the capbanks on it.
                    List<CapBankDevice> capBanksByFeeder = capControlCache.getCapBanksByFeeder(feeder.getCcId());
                    for (CapBankDevice capBankDevice : capBanksByFeeder) {
                        // There might be a better way to do this check!
                        LiteState capBankState = CapControlUtils.getCapBankState(capBankDevice.getControlStatus());
                        if (capBankState.getStateText().contains("Close")) {
                            feederActiveBankKVar += capBankDevice.getBankSize();
                            
                            for (Entry<Integer, Double> feederVoltage : feederVoltageRises.entrySet()) {
                                if( feederVoltage.getKey().equals(feeder.getCcId()) ) {
                                    feederVoltage.setValue(feederVoltage.getValue() + capBankDevice.getBankSize()/1200); // A 1200kVar bank gives 1V to everyone else on the same feeder. All other bank sizes scale proportionally.
                                } else {
                                    feederVoltage.setValue(feederVoltage.getValue() + capBankDevice.getBankSize()/1200/5); // A 1200kVar bank gives .2v to all other feeders. All other bank sizes scale proportionally.
                                }
                            }
                        }
                        
                        capBankToFeeder.put(capBankDevice.getCcId(), feeder.getCcId());
                    }
                    
                    // We can't generate a correct feeder Voltage yet, it is below (feeder.getCurrentVoltLoadPointID());
                    sendPoint(feeder.getCurrentVarLoadPointID(), currentSubBusBaseKVar/feedersBySubBus.size()-feederActiveBankKVar, PointType.Analog);
                    sendPoint(feeder.getCurrentWattLoadPointID(), currentSubBusBaseKw/feedersBySubBus.size(), PointType.Analog);

                    subBusKVarClosed += feederActiveBankKVar;
                }
                
                List<Zone> zonesBySubBusId = zoneService.getZonesBySubBusId(subBus.getCcId());
                
                for (Zone zone : zonesBySubBusId) {
                    List<CapBankToZoneMapping> capBankToZoneMappings = zoneService.getCapBankToZoneMapping(zone.getId());
                    
                    // We need to finish finding the zone feeder voltage offset, so we are going to do the Feeder->Regulator lookup here and add to feederVoltage based on the regulator.
                    // Note this only works here if we have a 1-1 zone to feeder mapping. Also note we are assuming the 3 phases of regulators are identical!
                    List<Integer> regulatorsForZone = zoneService.getRegulatorsForZone(zone.getId());
                    Integer regulatorForZone = regulatorsForZone.get(0);
                    if (regulatorForZone != 0) {
                        
                        if (getFeederForRegulatorFromCapbank(capBankToFeeder, capBankToZoneMappings.get(0)) != null) {
                            
                            Integer feederId = getFeederForRegulatorFromCapbank(capBankToFeeder, capBankToZoneMappings.get(0));
                            Double feederVoltage = feederVoltageRises.get( feederId );
                            feederVoltageRises.put(feederId, feederVoltage + 0.75*getRegulatorTapPosition(regulatorForZone));
                            //TODO instead of adding this to feederVoltageRises, create a Regulator to Phase and Feeder map and do the lookup whereever feederVoltageRises is used.
                            // This would allow for multi phase taps which appears to be necessary unfortunately.
                        }
                    }
                    
                    for (CapBankToZoneMapping capBankToZoneMapping : capBankToZoneMappings) {
                        Map<Integer, Phase> pointsForBankAndPhase = zoneService.getMonitorPointsForBankAndPhase(capBankToZoneMapping.getDeviceId());
                        
                        for (Entry<Integer, Phase> points : pointsForBankAndPhase.entrySet()) {
                            double voltage = getVoltageFromKwAndDistance(currentSubBusBaseKw, capBankToZoneMapping.getDistance() + zone.getGraphStartPosition() * 1000, MAX_KW);
                            voltage = shiftVoltageForPhase(voltage, points.getValue(), (capBankToZoneMapping.getDistance() + zone.getGraphStartPosition() * 1000));
                            
                            // Check if the bank itself is closed
                            CapBankDevice capBankDevice = capControlCache.getCapBankDevice(capBankToZoneMapping.getDeviceId());
                            LiteState capBankState = CapControlUtils.getCapBankState(capBankDevice.getControlStatus());
                            if(capBankState.getStateText().contains("Close")) {
                                voltage += (capBankDevice.getBankSize()/1200); // A bank gives a bonus extra voltage to itself.
                            }
                            
                            // Add in Feeder voltage shift
                            if (capBankToFeeder.get(capBankDevice.getCcId()) != null) {
                                voltage += feederVoltageRises.get(capBankToFeeder.get(capBankDevice.getCcId()));
                            }
                            
                            sendPoint(points.getKey(), voltage, PointType.Analog);
                        }
                        
                        //TODO Generate capbank points.
                    }
                    
                    for (Integer regulatorId : regulatorsForZone) {
                        
                        Regulator regulator = regulatorService.getRegulatorById(regulatorId);
                        Map<RegulatorPointMapping, Integer> mappings = regulator.getMappings();
                        for (Entry<RegulatorPointMapping, Integer> mapping : mappings.entrySet()) {
                            if (mapping.getValue() != 0) {
                                switch (mapping.getKey()) {
                                case AUTO_REMOTE_CONTROL:
                                    sendPoint(mapping.getValue(), 1, PointType.Status); // 1 is "remote"
                                    break;
                                case AUTO_BLOCK_ENABLE:
                                    sendPoint(mapping.getValue(), 0, PointType.Status); // No clue what 0 is here.
                                    break;
                                case TAP_POSITION:
                                    sendPoint(mapping.getValue(), getRegulatorTapPosition(regulatorId), PointType.Analog);
                                    break;
                                case VOLTAGE_X:
                                    sendPoint(mapping.getValue(), 120, PointType.Analog); // Yes, hardcoded 120V.
                                    break;
                                case KEEP_ALIVE:
                                    sendPoint(mapping.getValue(), keepAliveIncrementingValue, PointType.Analog);
                                    break;
                                case FORWARD_SET_POINT:
                                    sendPoint(mapping.getValue(), 120, PointType.Analog); // Really not used, here to generate point data
                                    break;
                                case FORWARD_BANDWIDTH:
                                    sendPoint(mapping.getValue(), 60, PointType.Analog); // Really not used, here to generate point data
                                    break;
                                // These are output points, no incoming value.
                                case TAP_UP:
                                case TAP_DOWN:
                                case TERMINATE:
                                    
                                case VOLTAGE_Y:
                                    // Voltage Y is special, and needs phase information. We are looking it up via zoneService and not using the mapping
                                    Map<Integer, Phase> pointsForRegulatorAndPhase =
                                    zoneService.getMonitorPointsForBankAndPhase(regulatorId);
                            
                                    double voltage = getVoltageFromKwAndDistance(currentSubBusBaseKw, zone.getGraphStartPosition() * 1000, MAX_KW);
                                    for (Entry<Integer, Phase> points : pointsForRegulatorAndPhase.entrySet()) {
                                        
                                        voltage = shiftVoltageForPhase(voltage, points.getValue(), zone.getGraphStartPosition() * 1000);
                                        
                                        // We need to map a Regulator to a Feeder, which is not a thing in CapControl. To simulate it, we will get a capbank from the zone, then 
                                        // find the Feeder from that CapBank. We will use this Feeder as the presumed Feeder for the Regulators on that same zone.
                                        // Add in Feeder voltage shift
                                        Integer feederId = getFeederForRegulatorFromCapbank(capBankToFeeder, capBankToZoneMappings.get(0));
                                        if (feederId != null) {
                                            voltage += feederVoltageRises.get(getFeederForRegulatorFromCapbank(capBankToFeeder, capBankToZoneMappings.get(0)));
                                            
                                            // The feeder voltage matches the regulator phase voltage. We finally have that so lets send it!
                                            if (points.getValue() == Phase.A) {
                                                Feeder feeder = capControlCache.getFeeder(feederId);
                                                sendPoint(feeder.getCurrentVoltLoadPointID(), voltage, PointType.Analog);
                                            }
                                        }
                                        
                                        sendPoint(points.getKey(), voltage, PointType.Analog);
                                    }
                                    break; 
                                default:
                                    break;
                                }
                            }
                        }
                    }
                }
                
                //Send out all of the sub bus points
                sendPoint(subBus.getCurrentVarLoadPointID(), currentSubBusBaseKVar - subBusKVarClosed, PointType.Analog);
                sendPoint(subBus.getCurrentVoltLoadPointID(), 120, PointType.Analog); // The sub bus Voltage is always 120
                sendPoint(subBus.getCurrentWattLoadPointID(), currentSubBusBaseKw, PointType.Analog);
                //sendPoint(subBus.getDailyOperationsAnalogPointId(), 1); Sent by C++ code.
            }
            
        }
    }

    // 1 volt shift down from A-C, .5 v shift from A-B. No shift for Phase A.
    // Shift is based on distance with full shift found at END_OF_LINE
    private double shiftVoltageForPhase(double voltage, Phase phase, double distance) {
        double pointMultiplier = (phase == Phase.A ? 0 : phase == Phase.B ? .5 : 1);
        return voltage - (pointMultiplier * distance / END_OF_LINE);
    }

    // Initialize the list of regulator tap positions from the database or Dispatch
    private void loadRegulatorTapPositionsFromDatabase() {
        if (!tapPositionsPreloaded) {
            DateTime start = DateTime.now();
            Set<Integer> regulatorTapPositionPointIds = new HashSet<>();
            Map<Integer, Integer> pointIdToRegulator = new HashMap<>();
            
            List<Area> simulatedAreas = capControlCache.getAreas().stream()
                                                                  .filter(area->area.getCcName().startsWith("Sim Area"))
                                                                  .collect(Collectors.toList());
            for (Area area : simulatedAreas) {
                List<SubBus> subBusesByArea = capControlCache.getSubBusesByArea(area.getCcId());
                for (SubBus subBus : subBusesByArea) {
                    List<Zone> zonesBySubBusId = zoneService.getZonesBySubBusId(subBus.getCcId());
                    for (Zone zone : zonesBySubBusId) {
                        List<Integer> regulatorsForZone = zoneService.getRegulatorsForZone(zone.getId());
                        for (Integer regulatorId : regulatorsForZone) {
                            
                            Regulator regulator = regulatorService.getRegulatorById(regulatorId);
                            Map<RegulatorPointMapping, Integer> mappings = regulator.getMappings();
                            for (Entry<RegulatorPointMapping, Integer> mapping : mappings.entrySet()) {
                                if (mapping.getValue() != 0 && mapping.getKey().equals(RegulatorPointMapping.TAP_POSITION)) {
                                    //sendPoint(mapping.getValue(), getRegulatorTapPosition(regulatorId), PointType.Analog);
                                    regulatorTapPositionPointIds.add(mapping.getValue());
                                    pointIdToRegulator.put(mapping.getValue(), regulatorId);
                                }
                            }
                        }
                    }
                }
            }

            int successCount = 0;
            
            if (regulatorTapPositionPointIds.size() > 0) {
                Set<? extends PointValueQualityHolder> tapPointValues = asyncDynamicDataSource.getPointValues(regulatorTapPositionPointIds);
                for (PointValueQualityHolder pointValueQualityHolder : tapPointValues) {
                    if (pointValueQualityHolder.getValue() < 30 && pointValueQualityHolder.getValue() > -20 && pointValueQualityHolder.getPointQuality().equals(PointQuality.Normal)) {
                        Integer regulatorId = pointIdToRegulator.get(pointValueQualityHolder.getId());
                        if (regulatorId != null) {
                            successCount++;
                            regulatorTapPositions.put(regulatorId, (int) pointValueQualityHolder.getValue());
                            
                        }
                    }
                }
                
                if (successCount > 0) {
                    tapPositionsPreloaded = true; // In theory we will keep trying until both capcontrol cache is loaded and Dispatch results come back.
                }
            }
            DateTime stop = DateTime.now();
            log.debug("Retrieved and loaded " + successCount + " tap point values from Dispatch in " + (stop.getMillis() - start.getMillis())/1000 + " seconds");
        }
    }
    
    // Returns the current regulator tap position, defaults to 3
    private Integer getRegulatorTapPosition(Integer regulatorId) { 
        if (!regulatorTapPositions.containsKey(regulatorId))
            regulatorTapPositions.put(regulatorId, INITIAL_TAP_POSITION);
        
        return regulatorTapPositions.get(regulatorId);
    }
    
    // We need to map a Regulator to a Feeder, which is not a thing in CapControl. To simulate it, we will get a capbank from the zone, then 
    // find the Feeder from that CapBank. We will use this Feeder as the presumed Feeder for the Regulators on that same zone.
    // Add in Feeder voltage shift
    private Integer getFeederForRegulatorFromCapbank(Map<Integer, Integer> capBankToFeeder, CapBankToZoneMapping capBankToZoneMapping) {
        return capBankToFeeder.get(capBankToZoneMapping.getDeviceId());
    }
    
    private double getVoltageFromKwAndDistance(double currentSubBusRawKw, double distance, int maxKw) {
        // for MAX KW we want a 6v drop at EOL, which we are defining as a distance of 40000
        // So if we are at half the distance (20k) we get half the drop (3v)
        // Or if we have much lower power (3kv) at 40k distance we get lower drop (1v or so)
        return 120-currentSubBusRawKw/maxKw*6*distance/END_OF_LINE;
    }
    
    // Sends this point to Dispatch. Does not force archive, follows point archival settings.
    private void sendPoint(int pointId, double value, PointType type) {
        if(pointId != 0) {
            PointData pointData = new PointData();
            pointData.setTagsPointMustArchive(false);    
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setId(pointId);
            pointData.setType(type.getPointTypeId());
            pointData.setValue(value);
            
            asyncDynamicDataSource.putValue(pointData);
        }
    }
}

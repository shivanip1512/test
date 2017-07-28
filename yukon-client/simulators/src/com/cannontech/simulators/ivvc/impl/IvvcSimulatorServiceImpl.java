package com.cannontech.simulators.ivvc.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
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
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.ivvc.model.IvvcSimulatorSettings;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.simulators.dao.RegulatorEventsSimulatorDao;
import com.cannontech.simulators.dao.RegulatorEventsSimulatorDao.RegulatorOperations;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.ivvc.IvvcSimulatorService;

public class IvvcSimulatorServiceImpl implements IvvcSimulatorService {
    private static final Logger log = YukonLogManager.getLogger(IvvcSimulatorServiceImpl.class);
    
    @Autowired private CapControlCache capControlCache;
    @Autowired private ZoneService zoneService;
    @Autowired private VoltageRegulatorService regulatorService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private RegulatorEventsSimulatorDao regulatorEventsSimulatorDao;
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private PointDao pointDao;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;

    private volatile boolean isRunning; // This refers to the simulator itself
    private volatile boolean isCurrentlyExecuting = false; // This is used to ensure only one timed execution happens at a time
    private boolean tapPositionsPreloaded = false;
    
    private final Map<Integer, Integer> regulatorTapPositions = new HashMap<>();
    private final Map<Integer, Integer> subBusKVar = new HashMap<>();
    private int keepAliveIncrementingValue = 0;
    private Instant lastRegulatorEvaluationTime;
    private ScheduledFuture<?> ivvcSimulationFuture;
    private final List<PointData> messagesToSend = new ArrayList<>();
    
    private final int END_OF_LINE = 40000;
    private final int INITIAL_TAP_POSITION = 3;
    
    private class PointTypeValue {
        private final int pointId;
        private final PointType type;
        private final double value;
        
        PointTypeValue(int id, PointType pointType, double pointValue) {
            pointId = id;
            type = pointType;
            value = pointValue;
        };
    }
    private final List<Integer> cbcPointsLoaded = new ArrayList<>();
    private final List<PointTypeValue> cbcPointCache = new ArrayList<>();
    
    @Override
    public boolean start(IvvcSimulatorSettings settings) {
        if (isRunning) {
            return false;
        } else {
            lastRegulatorEvaluationTime = Instant.now();
            ivvcSimulationFuture = executor.scheduleAtFixedRate(this::getSimulatorThread, 0, 30, TimeUnit.SECONDS);
            saveSettings(settings);
            log.info("IVVC simulator thread starting up.");
            isRunning = true;
            return true;
        }
    }
    
    @Override
    public void stop() {
        ivvcSimulationFuture.cancel(true);
        isRunning = false;
        log.info("IVVC simulator thread shutting down.");
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
                processNewRegulatorActions();
                calculateAndSendCapControlPoints();
            } catch (Exception e) {
                log.error("Error occurred in IVVC simulator.", e);
            }
        } else {
            log.error("Unable to execute IVVC Simulator this time, the thread is probably taking more than 30 seconds to run");
        }
        isCurrentlyExecuting = false;
    }
    
    // Load all of the tap up and down operations, add or subtract from tap position depending on operation performed.
    // TBD: Limits position?
    private void processNewRegulatorActions() {
        List<RegulatorOperations> regulatorTapOperations = regulatorEventsSimulatorDao.getRegulatorTapOperationsAfter(lastRegulatorEvaluationTime);
        
        if (!regulatorTapOperations.isEmpty()) {
            log.debug("Found " + regulatorTapOperations.size() + " tap operations, acting on them.");
        }
        
        for (RegulatorOperations regulatorOperations : regulatorTapOperations) {
            Integer tapPosition = Optional.ofNullable(regulatorTapPositions.get(regulatorOperations.regulatorId)).orElse(INITIAL_TAP_POSITION);
            
            if (regulatorOperations.eventType == EventType.TAP_UP) {
                regulatorTapPositions.put(regulatorOperations.regulatorId, tapPosition + 1);
            } else if (regulatorOperations.eventType == EventType.TAP_DOWN) {
                regulatorTapPositions.put(regulatorOperations.regulatorId, tapPosition - 1);
            }
            
            if (regulatorOperations.timeStamp.isAfter(lastRegulatorEvaluationTime)) {
                lastRegulatorEvaluationTime = regulatorOperations.timeStamp; 
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
        final int MIN_KVAR = -1500;
        final int MIN_KW = 3000;
        int secondsOfDayForCalculation = DateTime.now().plusHours(12).getSecondOfDay(); // This shifts the sine so the peak is at 18:00
        keepAliveIncrementingValue = (keepAliveIncrementingValue + 1) & 0xFFFF; // Rolls over after 16 bits.
        
        for (Area area : simulatedAreas) {
            List<SubBus> subBusesByArea = capControlCache.getSubBusesByArea(area.getCcId());
            subBusesByArea.parallelStream().forEach(subBus -> {
                
                Integer bankSize = subBusKVar.get(subBus.getCcId());
                if (bankSize == null) {
                    bankSize = capControlCache.getCapBanksBySubBus(subBus.getCcId()).stream().mapToInt(
                        capBank -> capBank.getBankSize()).sum();
                    subBusKVar.put(subBus.getCcId(), bankSize + 1500);
                }
                final int MAX_KVAR = bankSize;
                final int MAX_KW = (int) (MIN_KW + (MAX_KVAR - MIN_KVAR)/KW_TO_KVAR_MULTIPLIER);
                final double currentSubBusBaseKw = MIN_KW + (Math.sin(secondsOfDayForCalculation*2*Math.PI/DateTimeConstants.SECONDS_PER_DAY)*((MAX_KW-MIN_KW)/2) + ((MAX_KW-MIN_KW)/2));
                final double currentSubBusBaseKVar = (currentSubBusBaseKw - MIN_KW)*KW_TO_KVAR_MULTIPLIER + MIN_KVAR; //When we are at minimum kw, we are at MIN_KVAR
                
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
                    generatePoint(feeder.getCurrentVarLoadPointID(), currentSubBusBaseKVar/feedersBySubBus.size()-feederActiveBankKVar, PointType.Analog);
                    generatePoint(feeder.getCurrentWattLoadPointID(), currentSubBusBaseKw/feedersBySubBus.size(), PointType.Analog);

                    subBusKVarClosed += feederActiveBankKVar;
                }
                
                List<Zone> zonesBySubBusId = zoneService.getZonesBySubBusId(subBus.getCcId());
                
                for (Zone zone : zonesBySubBusId) {
                    List<CapBankToZoneMapping> capBankToZoneMappings = zoneService.getCapBankToZoneMapping(zone.getId());
                    Map<Phase, Integer> phaseToRegulator = new HashMap<>();

                    List<Integer> regulatorsForZone = zoneService.getRegulatorsForZone(zone.getId());
                    for (Integer regulatorId : regulatorsForZone) {
                        Regulator regulator = regulatorService.getRegulatorById(regulatorId);
                        Map<RegulatorPointMapping, Integer> mappings = regulator.getMappings();
                        for (Entry<RegulatorPointMapping, Integer> mapping : mappings.entrySet()) {
                            if (mapping.getValue() != 0) {
                                switch (mapping.getKey()) {
                                case AUTO_REMOTE_CONTROL:
                                    generatePoint(mapping.getValue(), 1, PointType.Status); // 1 is "remote"
                                    break;
                                case AUTO_BLOCK_ENABLE:
                                    generatePoint(mapping.getValue(), 0, PointType.Status); // No clue what 0 is here.
                                    break;
                                case TAP_POSITION:
                                    generatePoint(mapping.getValue(), getRegulatorTapPosition(regulatorId), PointType.Analog);
                                    break;
                                case VOLTAGE_X:
                                    generatePoint(mapping.getValue(), 120, PointType.Analog); // Yes, hardcoded 120V.
                                    break;
                                case KEEP_ALIVE:
                                    generatePoint(mapping.getValue(), keepAliveIncrementingValue, PointType.Analog);
                                    break;
                                case FORWARD_SET_POINT:
                                    generatePoint(mapping.getValue(), 120, PointType.Analog); // Really not used, here to generate point data
                                    break;
                                case FORWARD_BANDWIDTH:
                                    generatePoint(mapping.getValue(), 60, PointType.Analog); // Really not used, here to generate point data
                                    break;
                                case VOLTAGE_Y:
                                    // Voltage Y is special, and needs phase information. We are looking it up via zoneService and not using the mapping
                                    Map<Integer, Phase> pointsForRegulatorAndPhase =
                                    zoneService.getMonitorPointsForBankAndPhase(regulatorId);
                            
                                    double voltage = getVoltageFromKwAndDistance(currentSubBusBaseKw, zone.getGraphStartPosition() * 1000, MAX_KW);
                                    for (Entry<Integer, Phase> points : pointsForRegulatorAndPhase.entrySet()) {
                                        phaseToRegulator.put(points.getValue(), regulatorId); // We assign this regulator to phases based on the zone point assignment
                                        voltage = shiftVoltageForPhase(voltage, points.getValue(), zone.getGraphStartPosition() * 1000);
                                        
                                        // We need to map a Regulator to a Feeder, which is not a thing in CapControl. To simulate it, we will get a capbank from the zone, then 
                                        // find the Feeder from that CapBank. We will use this Feeder as the presumed Feeder for the Regulators on that same zone.
                                        // Add in Feeder voltage shift
                                        Integer feederId = getFeederForRegulatorFromCapbank(capBankToFeeder, capBankToZoneMappings.get(0));
                                        if (feederId != null) {
                                            voltage += feederVoltageRises.get(getFeederForRegulatorFromCapbank(capBankToFeeder, capBankToZoneMappings.get(0)));
                                            voltage += 0.75*getRegulatorTapPosition(phaseToRegulator.get(points.getValue()));

                                            // The feeder voltage matches the regulator phase voltage. We finally have that so lets send it!
                                            if (points.getValue() == Phase.A) {
                                                Feeder feeder = capControlCache.getFeeder(feederId);
                                                generatePoint(feeder.getCurrentVoltLoadPointID(), voltage, PointType.Analog);
                                            }
                                        }
                                        
                                        generatePoint(points.getKey(), voltage, PointType.Analog);
                                    }
                                    break; 
                                // These are output points, no incoming value.
                                case TAP_UP:
                                case TAP_DOWN:
                                case TERMINATE:
                                default:
                                    break;
                                }
                            }
                        }
                    }
                    
                    for (CapBankToZoneMapping capBankToZoneMapping : capBankToZoneMappings) {
                        Map<Integer, Phase> pointsForBankAndPhase = zoneService.getMonitorPointsForBankAndPhase(capBankToZoneMapping.getDeviceId());
                        
                        CapBankDevice capBankDevice = capControlCache.getCapBankDevice(capBankToZoneMapping.getDeviceId());
                        
                        for (Entry<Integer, Phase> points : pointsForBankAndPhase.entrySet()) {
                            double voltage = getVoltageFromKwAndDistance(currentSubBusBaseKw, capBankToZoneMapping.getDistance() + zone.getGraphStartPosition() * 1000, MAX_KW);
                            voltage = shiftVoltageForPhase(voltage, points.getValue(), (capBankToZoneMapping.getDistance() + zone.getGraphStartPosition() * 1000));
                            
                            // Check if the bank itself is closed
                            LiteState capBankState = CapControlUtils.getCapBankState(capBankDevice.getControlStatus());
                            if(capBankState.getStateText().contains("Close")) {
                                voltage += (capBankDevice.getBankSize()/1200); // A bank gives a bonus extra voltage to itself.
                            }
                            
                            // Add in Feeder voltage shift
                            if (capBankToFeeder.get(capBankDevice.getCcId()) != null) {
                                voltage += feederVoltageRises.get(capBankToFeeder.get(capBankDevice.getCcId()));
                            }
                            
                            // Add in regulator voltage shift
                            if (phaseToRegulator.get(points.getValue()) != null) {
                                voltage += 0.75*getRegulatorTapPosition(phaseToRegulator.get(points.getValue()));
                            }
                            
                            generatePoint(points.getKey(), voltage, PointType.Analog);
                        }
                        
                        addCbcPointsToCbcCache(capBankDevice.getControlDeviceID());
                    }
                    
                }
                
                //Send out all of the sub bus points
                generatePoint(subBus.getCurrentVarLoadPointID(), currentSubBusBaseKVar - subBusKVarClosed, PointType.Analog);
                generatePoint(subBus.getCurrentVoltLoadPointID(), 120, PointType.Analog); // The sub bus Voltage is always 120
                generatePoint(subBus.getCurrentWattLoadPointID(), currentSubBusBaseKw, PointType.Analog);
                //The daily operations point on the subBus is not sent by us, but sent by the C++ code. (subBus.getDailyOperationsAnalogPointId())
            });
            
        }
        
        generateCbcPoints();
        sendAllPointData();
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
            
            capControlCache.getAreas().stream()
                                      .filter(area->area.getCcName().startsWith("Sim Area"))
                                      .flatMap(area -> capControlCache.getSubBusesByArea(area.getCcId()).stream())
                                      .flatMap(subBus -> zoneService.getZonesBySubBusId(subBus.getCcId()).stream())
                                      .flatMap(zone -> zoneService.getRegulatorsForZone(zone.getId()).stream())
                                      .map(regulatorId -> regulatorService.getRegulatorById(regulatorId))
                                      .forEach((regulator -> { regulator.getMappings().entrySet().stream()
                                          .filter(mapping -> mapping.getValue() != 0 && mapping.getKey() == RegulatorPointMapping.TAP_POSITION)
                                          .forEach(tapPositionMapping -> {
                                              regulatorTapPositionPointIds.add(tapPositionMapping.getValue());
                                              pointIdToRegulator.put(tapPositionMapping.getValue(), regulator.getId());
                                              });
                                      }));
            
            int successCount = 0;
            
            if (regulatorTapPositionPointIds.size() > 0) {
                successCount = (int) asyncDynamicDataSource.getPointValues(regulatorTapPositionPointIds)
                                      .stream()
                                      .filter(pvqh -> pvqh.getValue() < 30 && pvqh.getValue() > -20)
                                      .filter(pvqh -> pvqh.getPointQuality() == PointQuality.Normal)
                                      .map(pvqh -> {
                                          Integer regulatorId = pointIdToRegulator.get(pvqh.getId());
                                          if (regulatorId != null) {
                                              regulatorTapPositions.put(regulatorId, (int) pvqh.getValue());
                                              return 1;
                                          }
                                          return 0;
                                      })
                                      .filter(i -> i == 1)
                                      .count();
                                     
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
    private void generatePoint(int pointId, double value, PointType type) {
        if(pointId != 0) {
            PointData pointData = new PointData();
            pointData.setTagsPointMustArchive(false);    
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setId(pointId);
            pointData.setType(type.getPointTypeId());
            pointData.setValue(value);
            
            if (pointData != null) {
                messagesToSend.add(pointData);
            }
        }
    }
    
    private void generateCbcPoints() {
        for (PointTypeValue pointTypeValue : cbcPointCache) {
            if (pointTypeValue != null) {
                generatePoint(pointTypeValue.pointId, pointTypeValue.value, pointTypeValue.type);    
            }
        }
    }
    
    // Send all point data. Note this is not thread safe.
    private void sendAllPointData() {
        final int chunkSize = 1000;
        if (!messagesToSend.isEmpty()) {
            int size = messagesToSend.size();
            for (int sent = 0; sent < size; sent += chunkSize) {
                asyncDynamicDataSource.putValues(messagesToSend.subList(sent, Math.min(sent+chunkSize, size)));
            }

            messagesToSend.clear();
            log.info("Sending " + size + " messages to Dispatch");
        }
    }
    
    private void addCbcPointsToCbcCache(int cbcId) {
        if (!cbcPointsLoaded.contains(cbcId)) {
            // We are going to try hard to only do this once per cbc simulator run as this is a pretty big lookup.
            cbcPointsLoaded.add(cbcId);
            Map<PointType, List<PointInfo>> cbcPointMap = pointDao.getAllPointNamesAndTypesForPAObject(cbcId);
            
            cbcPointCache.addAll(cbcPointMap.entrySet()
                                            .parallelStream()
                                            .flatMap(entry -> entry.getValue()
                                                                   .stream()
                                                                   .map(pointInfo -> getPointTypeAndValue(entry.getKey(), pointInfo)))
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList()));
        }
    }
    
    private PointTypeValue getPointTypeAndValue(PointType pointType, PointInfo pointInfo) {
        Double pointValue = null;
        
        switch (pointInfo.getName()) {
        case "Auto Close Delay Time":
        case "Bank Control Time":
        case "CBC-8000 Hardware Type ID":
        case "Daily Control Limit":
        case "Line Voltage THD":
        case "Voltage Control Flags":
            pointValue = 0.0;
            break;
        case "Average Line Voltage Time":
        case "Comms Loss Time":
        case "Manual Close Delay Time":
        case "Open Delay Time":
        case "Re-Close Delay Time":
            pointValue = 30.0;
            break;
        case "Comms Loss OV Threshold":
        case "CVR OV Threshold":
        case "Emergency OV Threshold":
        case "High Voltage":
        case "OV Threshold":
            pointValue = 128.0;
            break;
        case "Comms Loss UV Threshold":
        case "CVR UV Threshold":
        case "Emergency UV Threshold":
        case "Low Voltage":
        case "UV Threshold":
            pointValue = 118.0;
            break;
        case "Close Op Count":
        case "Open Op Count":
        case "OV Op Count":
        case "Total Op Count":
        case "UV Op Count":
        case "Delta Voltage":
            pointValue = 5.0;
            break;
        case "Temperature":
            pointValue = 88.0;
            break;
        // The below are all status points
        case "Abnormal Delta Voltage":
        case "Auto Control Mode":
        case "Bad Active Close Relay":
        case "Bad Active Trip Relay":
        case "Capacitor Bank State ":
        case "CVR Mode":
        case "Device Reset Indicator":
        case "Door Sensor Damage/Tamper":
        case "Door Status":
        case "Line Voltage High":
        case "Line Voltage Low":
        case "Manual Control Mode":
        case "Max Operation Count":
        case "Neutral Lockout":
        case "Operation Failed":
        case "Real Time Clock Battery":
        case "Reclose Block":
        case "Relay Sense Failed":
        case "Remote Control Mode":
        case "SCADA Override":
        case "Temperature High":
        case "Temperature Low":
            pointValue = 0.0;
            break;
        case "OVUV Control":
            pointValue = 1.0;
            break;

        default:
            break;
        }
        
        if (pointValue != null) {
            return new PointTypeValue(pointInfo.getPointId(), pointType, pointValue);
        }
        
        return null;
    }
    
    private void saveSettings(IvvcSimulatorSettings settings) {
        log.debug("Saving IVVC settings to YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_INCREASED_SPEED_MODE, settings.isIncreasedSpeedMode());
    }
    
    @Override
    public IvvcSimulatorSettings getCurrentSettings() {
        log.debug("Getting IVVC settings from db.");
        IvvcSimulatorSettings settings = new IvvcSimulatorSettings(yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_INCREASED_SPEED_MODE));
        return settings;
    }

    @Override
    public void startSimulatorWithCurrentSettings() {
        start(getCurrentSettings());
    }
}

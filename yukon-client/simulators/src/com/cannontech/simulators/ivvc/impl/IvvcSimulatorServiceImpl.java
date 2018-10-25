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
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.capcontrol.CapBankToZoneMapping;
import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.model.BankState;
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
import com.cannontech.simulators.RegulatorVoltageControlMode;
import com.cannontech.simulators.dao.DeviceConfigurationSimulatorDao;
import com.cannontech.simulators.dao.RegulatorEventsSimulatorDao;
import com.cannontech.simulators.dao.RegulatorEventsSimulatorDao.RegulatorOperations;
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.ivvc.IvvcSimulatorService;
import com.cannontech.yukon.IDatabaseCache;

public class IvvcSimulatorServiceImpl implements IvvcSimulatorService {
    private static final Logger log = YukonLogManager.getLogger(IvvcSimulatorServiceImpl.class);
    
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CapControlCache capControlCache;
    @Autowired private ZoneService zoneService;
    @Autowired private VoltageRegulatorService regulatorService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private RegulatorEventsSimulatorDao regulatorEventsSimulatorDao;
    @Autowired private DeviceConfigurationSimulatorDao deviceConfigurationSimulatorDao;
    @Autowired @Qualifier("main") private ScheduledExecutor executor;
    @Autowired private PointDao pointDao;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;

    private volatile boolean isRunning; // This refers to the simulator itself
    private volatile boolean isCurrentlyExecuting = false; // This is used to ensure only one timed execution happens at a time
    private volatile double substationBuskWh;
    private volatile boolean autoGenerateSubstationBuskWh = true;
    private volatile double localVoltageOffset;
    private volatile double remoteVoltageOffset;
    private volatile boolean tapPositionsAndSetPointValuesPreloaded = false;
    private volatile List<Integer> blockedPointIds = new ArrayList<>(); // The list of points that will not be sent
    private volatile List<Integer> badQualityPointIds = new ArrayList<>(); //The list of points that we want to force a bad quality on
    
    private final Map<Integer, Integer> regulatorTapPositions = new HashMap<>();
    private final Map<Integer, Double> regulatorSetPointValues = new HashMap<>();
    private final Map<Integer, Double> reverseSetPointValues = new HashMap<>();
    private final Map<Integer, Double> regulatorVoltageLoads = new HashMap<>();
    
    private final Map<Integer, Boolean> regulatorBackfedStatus = new HashMap<>();
    
    Map<Integer, RegulatorVoltageControlMode> regulatorVoltageControlModeConfig = new HashMap<>();
    private final Map<Integer, Integer> subBusKVar = new HashMap<>();
    private int keepAliveIncrementingValue = 0;
    private Instant lastRegulatorEvaluationTime;
    private ScheduledFuture<?> ivvcSimulationFuture;
    private final List<PointData> messagesToSend = new ArrayList<>();
    
    private final int END_OF_LINE = 40000;
    private final int SOLAR_LOCATION = 27000;
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
            substationBuskWh = settings.getSubstationBuskWh();
            autoGenerateSubstationBuskWh = settings.isAutoGenerateSubstationBuskWh();
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
    // Load all SetPoint positions and adjust the Tap correctly for SetPoint regulators
    private void processNewRegulatorActions() {

        // Get the Tap event and Set point operations for all regulators
        List<RegulatorOperations> regulatorEventOperations =
            regulatorEventsSimulatorDao.getRegulatorEventOperationsAfter(lastRegulatorEvaluationTime);

        // Adjust the tap positions as per the regulator voltage-control-modes
        if (!regulatorEventOperations.isEmpty()) {
            for (RegulatorOperations regulatorOperations : regulatorEventOperations) {
                if (regulatorVoltageControlModeConfig.get(regulatorOperations.regulatorId) == RegulatorVoltageControlMode.DIRECT_TAP) {
                    Integer tapPosition =
                        Optional.ofNullable(regulatorTapPositions.get(regulatorOperations.regulatorId)).orElse(
                            INITIAL_TAP_POSITION);
                    if (regulatorOperations.eventType == EventType.TAP_UP) {
                        regulatorTapPositions.put(regulatorOperations.regulatorId, tapPosition + 1);
                    } else if (regulatorOperations.eventType == EventType.TAP_DOWN) {
                        regulatorTapPositions.put(regulatorOperations.regulatorId, tapPosition - 1);
                    }
                } else if (regulatorVoltageControlModeConfig.get(regulatorOperations.regulatorId) == RegulatorVoltageControlMode.SET_POINT) {
                    if (regulatorOperations.eventType == EventType.DECREASE_SETPOINT
                        || regulatorOperations.eventType == EventType.INCREASE_SETPOINT) {
                        regulatorSetPointValues.put(regulatorOperations.regulatorId, regulatorOperations.setPointValue);
                    }
                }
                log.debug("Found " + regulatorEventOperations.size() + " tap and setpoint operations, acting on them.");
                if (regulatorOperations.timeStamp.isAfter(lastRegulatorEvaluationTime)) {
                    lastRegulatorEvaluationTime = regulatorOperations.timeStamp;
                }
            }
        }
        
        // This can cause changes based on the SetPoint, but also based on the changing voltage values
        // thus it needs to be called regardless of whether we received new SetPoint actions
        updateAllSetPointTapPositions();
    }
    
    private void calculateAndSendCapControlPoints() {
        List<Area> simulatedAreas = capControlCache.getAreas().stream()
                                                        .filter(area->area.getCcName().toLowerCase().startsWith("sim area"))
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
                
                final boolean backfedBus = subBus.getCcName().toLowerCase().startsWith("backfed"); //If the bus of a Sim Area starts with the word Backfed, the bus will be a backfed bus with power coming from a "solar" source.
                Integer bankSize = subBusKVar.get(subBus.getCcId());
                if (bankSize == null) {
                    bankSize = capControlCache.getCapBanksBySubBus(subBus.getCcId()).stream().mapToInt(
                        capBank -> capBank.getBankSize()).sum();
                    subBusKVar.put(subBus.getCcId(), bankSize + 1500);
                }
                final int MAX_KVAR = bankSize;
                final int MAX_KW = (int) (MIN_KW + (MAX_KVAR - MIN_KVAR)/KW_TO_KVAR_MULTIPLIER);
                double kWh = 0;
                if (autoGenerateSubstationBuskWh) {
                    kWh = MIN_KW + (Math.sin(secondsOfDayForCalculation*2*Math.PI/DateTimeConstants.SECONDS_PER_DAY)*((MAX_KW-MIN_KW)/2) + ((MAX_KW-MIN_KW)/2));
                } else {
                    kWh = substationBuskWh;
                }
                final double currentSubBusBaseKw = kWh;
                final double solarKw = currentSubBusBaseKw * 2/3;
                final double backfedDistance = END_OF_LINE - END_OF_LINE*solarKw/((MAX_KW+solarKw));
                final double currentSubBusBaseKVar = (currentSubBusBaseKw - MIN_KW)*KW_TO_KVAR_MULTIPLIER + MIN_KVAR; //When we are at minimum kw, we are at MIN_KVAR
                
                int subBusKVarClosed = 0;
                Map<Integer, Integer> capBankToFeeder = new HashMap<>();
                List<Feeder> feedersBySubBus = capControlCache.getFeedersBySubBus(subBus.getCcId());
                // Feeder voltage rises affect every device on the feeder and need a bunch of data to calculate. 
                // We gather them here until we finally have the right value and use it everywhere.
                Map<Integer, Double> feederVoltageRises = feedersBySubBus.stream()
                                                                         .collect(Collectors.toMap(feeder->feeder.getCcId(), 
                                                                                                   feeder->0.0));
                
                List<Zone> zonesBySubBusId = zoneService.getZonesBySubBusId(subBus.getCcId());
                
                // if there are no zones under this substation, it means the strategy is not IVVC.
                boolean isIvvcStrategy = true;
                if (CollectionUtils.isEmpty(zonesBySubBusId)) {
                    isIvvcStrategy = false;
                }
                
                for (Feeder feeder : feedersBySubBus) {
                    int feederActiveBankKVar = 0;
                    
                    // Record the feeder kVar shift and Voltage shift from the capbanks on it.
                    List<CapBankDevice> capBanksByFeeder = capControlCache.getCapBanksByFeeder(feeder.getCcId());
                    for (CapBankDevice capBankDevice : capBanksByFeeder) {
                        // There might be a better way to do this check!
                        LiteState capBankState = CapControlUtils.getCapBankState(capBankDevice.getControlStatus());
                        if (capBankState != null && isCapBankInOneOfCloseStates(capBankState.getStateRawState())) {
                            feederActiveBankKVar += capBankDevice.getBankSize();
                            
                            for (Entry<Integer, Double> feederVoltage : feederVoltageRises.entrySet()) {
                                if( feederVoltage.getKey().equals(feeder.getCcId()) ) {
                                    feederVoltage.setValue(
                                        feederVoltage.getValue() + capBankDevice.getBankSize() / localVoltageOffset); // A local Voltage Offset (KVar) bank gives 1V to everyone else on the same feeder. All other bank sizes scale proportionally.
                                } else {
                                    feederVoltage.setValue(feederVoltage.getValue()
                                        + capBankDevice.getBankSize() / remoteVoltageOffset / 5); // A remote Voltage Offset (KVar) bank gives .2v to all other feeders. All other bank sizes scale proportionally.
                                }
                            }
                        }
                        
                        capBankToFeeder.put(capBankDevice.getCcId(), feeder.getCcId());
                        
                        // IF the strategy is not IVVC, generate CBC points
                        if (!isIvvcStrategy) {
                            Map<Integer, Phase> pointsForBankAndPhase = zoneService.getMonitorPointsForBankAndPhase(capBankDevice.getCcId());
                            
                            for (Entry<Integer, Phase> points : pointsForBankAndPhase.entrySet()) {
                                double voltage = getVoltageFromKwAndDistance(currentSubBusBaseKw, 0, MAX_KW);
                                voltage = shiftVoltageForPhase(voltage, points.getValue(), 0);
                                
                                if (capBankState != null && isCapBankInOneOfCloseStates(capBankState.getStateRawState())) {
                                    voltage += (capBankDevice.getBankSize() / localVoltageOffset); // A bank gives a bonus extra voltage to itself.
                                }
                                
                                // Add in Feeder voltage shift
                                if (capBankToFeeder.get(capBankDevice.getCcId()) != null) {
                                    voltage += feederVoltageRises.get(capBankToFeeder.get(capBankDevice.getCcId()));
                                }
                                
                                generatePoint(points.getKey(), voltage, PointType.Analog);
                            }
                            int cbcId = capBankDevice.getControlDeviceID();
                            String cbcName = dbCache.getAllPaosMap().get(cbcId).getPaoName();
                            addCbcPointsToCbcCache(cbcId, cbcName);
                        }
                    }
                    
                    //If the strategy is NOT IVVC, we need to generate feeder voltage points here.
                    if (!isIvvcStrategy) {
                        generatePoint(feeder.getCurrentVoltLoadPointID(),
                            120 + feederVoltageRises.get(feeder.getCcId()), PointType.Analog);
                    }
                    
                    // We can't generate a correct feeder Voltage yet, it is below (feeder.getCurrentVoltLoadPointID());
                    generatePoint(feeder.getCurrentVarLoadPointID(), currentSubBusBaseKVar/feedersBySubBus.size()-feederActiveBankKVar, PointType.Analog);
                    generatePoint(feeder.getCurrentWattLoadPointID(), currentSubBusBaseKw/feedersBySubBus.size(), PointType.Analog);

                    subBusKVarClosed += feederActiveBankKVar;
                }
                
                for (Zone zone : zonesBySubBusId) {
                    List<CapBankToZoneMapping> capBankToZoneMappings = zoneService.getCapBankToZoneMapping(zone.getId());
                    Map<Phase, Integer> phaseToRegulator = new HashMap<>();

                    List<Integer> regulatorsForZone = zoneService.getRegulatorsForZone(zone.getId());
                    for (Integer regulatorId : regulatorsForZone) {
                        Regulator regulator = regulatorService.getRegulatorById(regulatorId);
                        Map<RegulatorPointMapping, Integer> mappings = regulator.getMappings();
                        for (Entry<RegulatorPointMapping, Integer> mapping : mappings.entrySet()) {
                            if (mapping.getValue() != null && mapping.getValue() != 0) {
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
                                case SOURCE_VOLTAGE:
                                    generatePoint(mapping.getValue(), 120, PointType.Analog); // Yes, hardcoded 120V.
                                    break;
                                case KEEP_ALIVE:
                                    generatePoint(mapping.getValue(), keepAliveIncrementingValue, PointType.Analog);
                                    break;
                                case FORWARD_SET_POINT:
                                    if (regulatorVoltageControlModeConfig.get(regulatorId)== RegulatorVoltageControlMode.SET_POINT &&
                                            regulatorSetPointValues.containsKey(regulatorId)) {
                                        generatePoint(mapping.getValue(), regulatorSetPointValues.get(regulatorId),
                                            PointType.Analog); // Really not used, here to generate point data
                                    } else {
                                        generatePoint(mapping.getValue(), 120, PointType.Analog); // Really not used, here to generate point data
                                    }
                                    break;
                                case FORWARD_BANDWIDTH:
                                    generatePoint(mapping.getValue(), 2, PointType.Analog); // Really not used, here to generate point data
                                    break;
                                case REVERSE_SET_POINT:
                                    if (regulatorVoltageControlModeConfig.get(regulatorId)== RegulatorVoltageControlMode.SET_POINT &&
                                        reverseSetPointValues.containsKey(regulatorId)) {
                                        generatePoint(mapping.getValue(), reverseSetPointValues.get(regulatorId),
                                            PointType.Analog); // Really not used, here to generate point data
                                    } else {
                                        generatePoint(mapping.getValue(), 120, PointType.Analog); // Really not used, here to generate point data
                                    }
                                    break;
                                case REVERSE_BANDWIDTH:
                                    generatePoint(mapping.getValue(), 2, PointType.Analog); // Really not used, here to generate point data
                                    break;
                                case REVERSE_FLOW_INDICATOR:
                                    if (backfedBus) {
                                        regulatorBackfedStatus.put(regulatorId, zone.getGraphStartPosition() >= backfedDistance/1000);
                                    }
                                    break;
                                case VOLTAGE:
                                    // Voltage is special, and needs phase information. We are looking it up via zoneService and not using the mapping
                                    Map<Integer, Phase> pointsForRegulatorAndPhase =
                                    zoneService.getMonitorPointsForBankAndPhase(regulatorId);
                                    
                                    double voltage = getVoltageFromKwAndDistance(currentSubBusBaseKw, zone.getGraphStartPosition() * 1000, MAX_KW);
                                    if (backfedBus) {
                                        voltage = this.solarVoltageBump(voltage, currentSubBusBaseKw, zone.getGraphStartPosition() * 1000, MAX_KW);
                                    }
                                    for (Entry<Integer, Phase> points : pointsForRegulatorAndPhase.entrySet()) {
                                        phaseToRegulator.put(points.getValue(), regulatorId); // We assign this regulator to phases based on the zone point assignment
                                        voltage = shiftVoltageForPhase(voltage, points.getValue(), zone.getGraphStartPosition() * 1000);
                                        
                                        // We need to map a Regulator to a Feeder, which is not a thing in CapControl. To simulate it, we will get a capbank from the zone, then 
                                        // find the Feeder from that CapBank. We will use this Feeder as the presumed Feeder for the Regulators on that same zone.
                                        // Add in Feeder voltage shift
                                        if (CollectionUtils.isNotEmpty(capBankToZoneMappings)) {
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
                                        }
                                        
                                        generatePoint(points.getKey(), voltage, PointType.Analog);
                                        regulatorVoltageLoads.put(regulatorId, voltage);
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
                            
                            if (backfedBus) {
                                voltage = this.solarVoltageBump(voltage, currentSubBusBaseKw, capBankToZoneMapping.getDistance() + zone.getGraphStartPosition() * 1000, MAX_KW);
                            }
                            
                            // Check if the bank itself is closed
                            LiteState capBankState = CapControlUtils.getCapBankState(capBankDevice.getControlStatus());
                            if(capBankState != null && isCapBankInOneOfCloseStates(capBankState.getStateRawState())) {
                                voltage += (capBankDevice.getBankSize() / localVoltageOffset); // A bank gives a bonus extra voltage to itself.
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
                        
                        int cbcId = capBankDevice.getControlDeviceID();
                        String cbcName = dbCache.getAllPaosMap().get(cbcId).getPaoName();
                        addCbcPointsToCbcCache(cbcId, cbcName);
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
        // pre-load tap positions and set point values from Dispatch.
        if (!tapPositionsAndSetPointValuesPreloaded) {
            DateTime start = DateTime.now();
            Set<Integer> regulatorTapPositionPointIds = new HashSet<>();
            Map<Integer, Integer> pointIdToRegulator = new HashMap<>();
            Set<Integer> setPointValuePointIds = new HashSet<>();
            Set<Integer> reverseSetPointValuePointIds = new HashSet<>();
            Map<Integer, Integer> setPointValueToRegulator = new HashMap<>();
            Map<Integer, Integer> reverseSetPointValueToRegulator = new HashMap<>();
            capControlCache.getAreas().stream()
                                      .filter(area->area.getCcName().startsWith("Sim Area"))
                                      .flatMap(area -> capControlCache.getSubBusesByArea(area.getCcId()).stream())
                                      .flatMap(subBus -> zoneService.getZonesBySubBusId(subBus.getCcId()).stream())
                                      .flatMap(zone -> zoneService.getRegulatorsForZone(zone.getId()).stream())
                                      .map(regulatorId -> regulatorService.getRegulatorById(regulatorId))
                                      .forEach((regulator -> { regulator.getMappings().entrySet().stream()
                                          .forEach(mapping -> {
                                              if (mapping.getValue() != null) {
                                                  if (mapping.getValue() != 0 && mapping.getKey() == RegulatorPointMapping.TAP_POSITION) {
                                                      regulatorTapPositionPointIds.add(mapping.getValue());
                                                      pointIdToRegulator.put(mapping.getValue(), regulator.getId());
                                                  } else if (mapping.getValue() != 0 && mapping.getKey() == RegulatorPointMapping.FORWARD_SET_POINT) {
                                                      setPointValuePointIds.add(mapping.getValue());
                                                      setPointValueToRegulator.put(mapping.getValue(), regulator.getId());
                                                  } else if (mapping.getValue() != 0 && mapping.getKey() == RegulatorPointMapping.REVERSE_SET_POINT) {
                                                      reverseSetPointValuePointIds.add(mapping.getValue());
                                                      reverseSetPointValueToRegulator.put(mapping.getValue(), regulator.getId());
                                                  }
                                              }
                                          });
                                          regulatorVoltageControlModeConfig.put(regulator.getId(), null);
                                    }));
            
            int tapPositionsLoadedSuccessCount = 0;
            
            if (regulatorTapPositionPointIds.size() > 0) {
                tapPositionsLoadedSuccessCount = (int) asyncDynamicDataSource.getPointValues(regulatorTapPositionPointIds)
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
            }
            
            int setPointValuesLoadedsuccessCount = 0;
            
            if (setPointValuePointIds.size() > 0) {
                setPointValuesLoadedsuccessCount = (int) asyncDynamicDataSource.getPointValues(setPointValuePointIds)
                                      .stream()
                                      .filter(setPoint -> setPoint.getPointQuality() == PointQuality.Normal)
                                      .map(setPoint -> {
                                          Integer regulatorId = setPointValueToRegulator.get(setPoint.getId());
                                          if (regulatorId != null) {
                                              regulatorSetPointValues.put(regulatorId, setPoint.getValue());
                                              return 1;
                                          }
                                          return 0;
                                      })
                                      .filter(i -> i == 1)
                                      .count();
            }
            
            int reverseSetPointValuesLoadedsuccessCount = 0;
            
            if (reverseSetPointValuePointIds.size() > 0) {
                reverseSetPointValuesLoadedsuccessCount = (int) asyncDynamicDataSource.getPointValues(reverseSetPointValuePointIds)
                                      .stream()
                                      .filter(setPoint -> setPoint.getPointQuality() == PointQuality.Normal)
                                      .map(setPoint -> {
                                          Integer regulatorId = reverseSetPointValueToRegulator.get(setPoint.getId());
                                          if (regulatorId != null) {
                                              reverseSetPointValues.put(regulatorId, setPoint.getValue());
                                              return 1;
                                          }
                                          return 0;
                                      })
                                      .filter(i -> i == 1)
                                      .count();
            }
            
            if ((CollectionUtils.isNotEmpty(regulatorTapPositionPointIds) && tapPositionsLoadedSuccessCount > 0)
                && (CollectionUtils.isNotEmpty(setPointValuePointIds) && setPointValuesLoadedsuccessCount > 0)
                && (CollectionUtils.isNotEmpty(reverseSetPointValuePointIds) && reverseSetPointValuesLoadedsuccessCount > 0)) {
                tapPositionsAndSetPointValuesPreloaded = true;
            }
            
            DateTime stop = DateTime.now();
            log.debug("Retrieved and loaded " + tapPositionsLoadedSuccessCount + " tap point values and "
                + setPointValuesLoadedsuccessCount + " set point values from Dispatch in "
                + (stop.getMillis() - start.getMillis()) / 1000 + " seconds");
        }
        
        // Load regulator voltage control modes from DB every 30 seconds
        deviceConfigurationSimulatorDao.getDeviceVoltageControlMode(regulatorVoltageControlModeConfig);
    }
    
    /**
     * Updates all set point positions based on the current position and voltages. This is intended
     * to be called after SetPoint positions are updated from the db and before new voltages are 
     * calculated. This is doing its best to mimic a real regulator with a 30 second response delay.
     * Note a real regulator actually waits 1-2 minutes before responding to a voltage and it looks
     * at the voltage more than once before committing to a change.
     */
    private void updateAllSetPointTapPositions() {
        for (Integer regulatorId : regulatorSetPointValues.keySet()) {
            final Double BANDWIDTH = 2.0;
            RegulatorVoltageControlMode configControlType = regulatorVoltageControlModeConfig.get(regulatorId);
            if (configControlType != null && configControlType == RegulatorVoltageControlMode.SET_POINT) {
                Double voltageLoad = regulatorVoltageLoads.get(regulatorId);
                if (voltageLoad != null && regulatorSetPointValues.containsKey(regulatorId)) {
                    int tapChange;
                    if (regulatorBackfedStatus.containsKey(regulatorId) && regulatorBackfedStatus.get(regulatorId) == true) {
                        tapChange = getSetPointTapChange(reverseSetPointValues.get(regulatorId), voltageLoad, BANDWIDTH);
                    } else {
                        tapChange = getSetPointTapChange(regulatorSetPointValues.get(regulatorId), voltageLoad, BANDWIDTH);
                    }
                    regulatorTapPositions.put(regulatorId, getRegulatorTapPosition(regulatorId) + tapChange);

                }
            }
        }
    }
    
    /*
     * In SetPoint the regulator is given a voltage it should target. The regulator taps to keep itself
     * near the voltage but stops once the voltage is within the bandwidth. This means it will not tap
     * all the way to the exact setpoint, but only close to it. It also does not tap optimally close but
     * stops immediately once it reaches a value within the range of SetPointVoltage += (bandwidth/2). 
     * 
     * So for example of the setpoint is at 123 and this voltage value before taps is 120 but the regulator 
     * currently has 3 taps the real voltage is 122.25. Without knowing the current voltage, we do not know if 
     * the tap value should be 3, 4, or 5 - All 3 of those values are legal and fit within the bandwidth. 
     * The only way to know which one it should be is to have followed the value as the voltages either went 
     * up or down. If the setpoint was higher and is now being lowered, the taps would be 4 or 5, but if it 
     * was lower and was being raised the taps would be 3 or 4.
     */
    private static int getSetPointTapChange(Double setPointVoltage, Double currentVoltage, Double bandwidth) {
        int tapChange = 0;
        if (Math.abs(setPointVoltage - currentVoltage) > (bandwidth / 2)) {
            tapChange = (int) (1
                + Math.floor((Math.abs(setPointVoltage - currentVoltage) - (bandwidth / 2)) / 0.75));
            tapChange = (int) (tapChange * Math.signum(setPointVoltage - currentVoltage));
        }
        return tapChange;
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
    
    private double solarVoltageBump(double voltage, double currentSubBusRawKw, double distance, int maxKw) {
        return voltage + currentSubBusRawKw*2/maxKw*(END_OF_LINE-Math.abs(distance-SOLAR_LOCATION))/END_OF_LINE;
    }
    
    // Sends this point to Dispatch. Does not force archive, follows point archival settings.
    // May not send points or may modify quality based on simulator settings
    private void generatePoint(int pointId, double value, PointType type) {
        if(pointId != 0 && !blockedPointIds.contains(pointId)) {
            PointData pointData = new PointData();
            pointData.setTagsPointMustArchive(false);    
            pointData.setPointQuality(PointQuality.Normal);
            pointData.setId(pointId);
            pointData.setType(type.getPointTypeId());
            pointData.setValue(value);
            
            if (badQualityPointIds.contains(pointId)) {
                pointData.setPointQuality(PointQuality.NonUpdated);
            }
            
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
    
    private void addCbcPointsToCbcCache(int cbcId, String cbcName) {
        // The second condition is prevent point data generation for user created CBC by not adding points for such CBC 
        // to the cbcPointCache
        if (!cbcPointsLoaded.contains(cbcId) && StringUtils.startsWith(cbcName, "Sim CBC")) {
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
    
    public void saveSettings(IvvcSimulatorSettings settings) {
        log.debug("Saving IVVC settings to YukonSimulatorSettings table.");
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_INCREASED_SPEED_MODE, settings.isIncreasedSpeedMode());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_SUBSTATION_BUS_KWH, settings.getSubstationBuskWh());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_AUTOGENERATE_SUBSTATION_BUS_KWH, settings.isAutoGenerateSubstationBuskWh());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_LOCAL_VOLTAGE_OFFSET_VAR, settings.getLocalVoltageOffsetVar());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_REMOTE_VOLTAGE_OFFSET_VAR, settings.getRemoteVoltageOffsetVar());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_BLOCKED_POINTS, settings.getBlockedPoints());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_BAD_QUALITY_POINTS, settings.getBadQualityPoints());
        
        substationBuskWh = settings.getSubstationBuskWh();
        autoGenerateSubstationBuskWh = settings.isAutoGenerateSubstationBuskWh();
        localVoltageOffset = settings.getRemoteVoltageOffsetVar();
        remoteVoltageOffset = settings.getRemoteVoltageOffsetVar();
        blockedPointIds = Stream.of(settings.getBlockedPoints().split(","))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        badQualityPointIds = Stream.of(settings.getBadQualityPoints().split(","))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
    
    @Override
    public IvvcSimulatorSettings getCurrentSettings() {
        log.debug("Getting IVVC settings from db.");
        IvvcSimulatorSettings settings = new IvvcSimulatorSettings(
            yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_INCREASED_SPEED_MODE),
            yukonSimulatorSettingsDao.getDoubleValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_SUBSTATION_BUS_KWH),
            yukonSimulatorSettingsDao.getBooleanValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_AUTOGENERATE_SUBSTATION_BUS_KWH),
            yukonSimulatorSettingsDao.getDoubleValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_LOCAL_VOLTAGE_OFFSET_VAR),
            yukonSimulatorSettingsDao.getDoubleValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_REMOTE_VOLTAGE_OFFSET_VAR));
        settings.setBlockedPoints(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_BLOCKED_POINTS));
        settings.setBadQualityPoints(yukonSimulatorSettingsDao.getStringValue(YukonSimulatorSettingsKey.IVVC_SIMULATOR_BAD_QUALITY_POINTS));
        return settings;
    }

    @Override
    public void startSimulatorWithCurrentSettings() {
        start(getCurrentSettings());
    }
    
    private boolean isCapBankInOneOfCloseStates(int rawState) {
        return rawState == BankState.CLOSE.getRawState() || rawState == BankState.CLOSE_FAIL.getRawState()
            || rawState == BankState.CLOSE_PENDING.getRawState()
            || rawState == BankState.CLOSE_QUESTIONABLE.getRawState();
    }
}

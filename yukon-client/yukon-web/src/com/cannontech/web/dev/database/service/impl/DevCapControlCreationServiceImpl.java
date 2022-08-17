package com.cannontech.web.dev.database.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.creation.service.CapControlCreationService;
import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneThreePhase;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.CategoryType;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.model.CompleteDevice;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.CapControlStrategy.DayOfWeek;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.database.db.capcontrol.VoltViolationType;
import com.cannontech.database.db.capcontrol.VoltageViolationSetting;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.database.model.Season;
import com.cannontech.development.model.DevCommChannel;
import com.cannontech.development.model.DevPaoType;
import com.cannontech.development.service.impl.DevObjectCreationBase;
import com.cannontech.simulators.RegulatorVoltageControlMode;
import com.cannontech.web.capcontrol.service.BusService;
import com.cannontech.web.capcontrol.service.CapBankService;
import com.cannontech.web.capcontrol.service.FeederService;
import com.cannontech.web.capcontrol.service.StrategyService;
import com.cannontech.web.dev.database.objects.DevCapControl;
import com.cannontech.web.dev.database.service.DevCapControlCreationService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class DevCapControlCreationServiceImpl extends DevObjectCreationBase implements DevCapControlCreationService {
    @Autowired private CapControlCreationService capControlCreationService;
    @Autowired private SubstationDao substationDao;
    @Autowired private CapbankDao capbankDao;
    @Autowired private FeederDao feederDao;
    @Autowired private SubstationBusDao substationBusDao;
    @Autowired private CapbankControllerDao capbankControllerDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private ZoneService zoneService;
    @Autowired private StrategyService strategyService;
    @Autowired private SeasonScheduleDao seasonScheduleDao;
    @Autowired private CapBankService capbankService;
    @Autowired private BusService busService;
    @Autowired private PointCreationService pointCreationService;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private FeederService feederService;
    @Autowired private VoltageRegulatorService voltageRegulatorService;
    @Autowired private DeviceConfigurationService deviceConfigurationService;
    @Autowired private DeviceConfigurationService deviceConfigService;

    private static final ReentrantLock _lock = new ReentrantLock();
    private static int complete;
    private static int total;
    private static int setPointRegulatorConfigId;
    
    private final static ImmutableSet<PaoType> threePhaseCbcTypes = ImmutableSet.of(PaoType.CBC_8020, PaoType.CBC_8024);

    @Override
    public boolean isRunning() {
        return _lock.isLocked();
    }

    @Override
    //@Transactional -- This was formerly transactional, but saveSeasonStrategyAssigment uses an internal cache and that
    //cache could not update since this transaction blocked it from reading in the changes.
    public void executeSetup(DevCapControl devCapControl) {
        if (_lock.tryLock()) {
            try {
                total = devCapControl.getTotal();
                complete = 0;
                createCapControl(devCapControl);
            } finally {
                _lock.unlock();
            }
        }
    }
    
    @Override
    public int getPercentComplete() {
        if (total >= 1) {
            return (complete*100) / total;
        } else { 
            return 0;
        }
    }
    
    private void createCapControl(DevCapControl devCapControl) {
        log.info("Creating (and assigning) Cap Control Objects ...");
        int strategyId = createCapControlStrategy(devCapControl);
        
        if (devCapControl.getRegulatorVoltageControlMode() == RegulatorVoltageControlMode.SET_POINT
            || devCapControl.getRegulatorVoltageControlMode() == RegulatorVoltageControlMode.BOTH) {
            setPointRegulatorConfigId = createDeviceConfigurationForSetPointRegulator(devCapControl);
        }

        for (int areaIndex = 0; areaIndex  < devCapControl.getNumAreas(); areaIndex++) { // Areas
            PaoIdentifier areaPao = createArea(devCapControl, areaIndex);
            for (int subIndex = 0; subIndex < devCapControl.getNumSubs(); subIndex++) { // Substations
                
                PaoIdentifier substationPao = createSubstation(devCapControl, areaIndex, areaPao , subIndex);
                
                CompleteYukonPao substationPointHolder =  new CompleteDevice();
                substationPointHolder.setPaoName("Sim RTU "  + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex));
                paoPersistenceService.createPaoWithDefaultPoints(substationPointHolder, PaoType.VIRTUAL_SYSTEM);
                        
                int pointToSubBusSimRtuPointOffset = 1;
                int pointToFeederSimRtuPointOffset = 1;
                int pointToRegulatorSimRtuPointOffset = 1;
                
                for (int subBusIndex = 0; subBusIndex < devCapControl.getNumSubBuses(); subBusIndex++) { // Substations Buses
                    
                    PaoIdentifier subBusPao =  createAndAssignSubstationBus(devCapControl, areaIndex, subIndex, substationPao , subBusIndex, strategyId);
                    pointToSubBusSimRtuPointOffset = attachPointsToSubBus(pointToSubBusSimRtuPointOffset, subBusPao, substationPointHolder.getPaoIdentifier());
                    
                    Integer parentZoneId = null;
                    
                    final double graphPositionOffset = 4.5; // How apart each regulator (zone) or cbc are
                    
                    for (int feederIndex = 0; feederIndex < devCapControl.getNumFeeders(); feederIndex++) { // Feeders
                        List<ZoneAssignmentCapBankRow> bankAssignments = new ArrayList<>();
                        PaoIdentifier feederPao = createAndAssignFeeder(devCapControl, areaIndex, subIndex, subBusIndex, subBusPao, feederIndex);
                        pointToFeederSimRtuPointOffset = attachPointsToFeeder(pointToFeederSimRtuPointOffset, feederPao, substationPointHolder.getPaoIdentifier());
                        
                        double graphPosition = graphPositionOffset; // each bank is relative to the start of the zone it is assigned to
                        for (int capBankIndex = 0; capBankIndex < devCapControl.getNumCapBanks(); capBankIndex++) { // CapBanks & CBCs
                            PaoIdentifier capBankPao = createAndAssignCapBank(devCapControl, areaIndex, subIndex, subBusIndex, feederIndex, feederPao, capBankIndex, bankAssignments, graphPosition);
                            graphPosition += graphPositionOffset;
                            createAndAssignCBC(devCapControl, areaIndex, subIndex, subBusIndex, feederIndex, capBankIndex, capBankPao);
                            assignCBCVoltagePointsToBank(capBankPao.getPaoId(), devCapControl.getCbcType().getPaoType());
                        }
                        
                        if (devCapControl.isUseIvvcControlType()) {
                            List<PaoIdentifier> regulatorsForZone = new ArrayList<>();
                            for (int regulatorIndex = 0; regulatorIndex < 3; regulatorIndex++) {
                                PaoIdentifier regulatorPao = createRegulatorForSubBus(devCapControl, areaIndex, subIndex, subBusIndex, feederIndex, regulatorIndex);
                                pointToRegulatorSimRtuPointOffset = attachPointsToRegulator(pointToRegulatorSimRtuPointOffset, regulatorPao, substationPointHolder.getPaoIdentifier(), subIndex, setPointRegulatorConfigId, devCapControl.getRegulatorVoltageControlMode());
                                regulatorsForZone.add(regulatorPao);
                            }
                            
                            // The first time through the zone is the parent (no parent zone ID), each time after its parent is the previous zone.
                            double zonePosition = graphPositionOffset * feederIndex * (devCapControl.getNumCapBanks()+1); // Zones start at 0 (which includes the regulator), then are the capbanks, then another zone. The regulator adds the +1.
                            parentZoneId = createAndAssignZone(devCapControl, areaIndex, subIndex, subBusPao, subBusIndex, regulatorsForZone, bankAssignments, parentZoneId, zonePosition);
                        }
                    }
                }
            }
        }
        
        if (devCapControl.isUseIvvcControlType()) {
            for (int regIndex = 0; regIndex < devCapControl.getNumRegulators(); regIndex++) { // Additional Regulators
                createRegulators(devCapControl, regIndex);
            }
        }
    }

    private int attachPointsToSubBus(int pointOffset, PaoIdentifier subBusPao, PaoIdentifier rtuPao) {
        CapControlSubBus capControlSubBus = busService.get(subBusPao.getPaoId());
        
        PointBase voltLoadPoint = createAnalogPoint(capControlSubBus.getName() + " Volt Point", UnitOfMeasure.VOLTS, pointOffset++, rtuPao, PointUnit.DEFAULT_DECIMAL_PLACES);
        PointBase kwPoint = createAnalogPoint(capControlSubBus.getName() + " kWatt Point", UnitOfMeasure.KW, pointOffset++, rtuPao, PointUnit.DEFAULT_DECIMAL_PLACES);
        PointBase kvarPoint = createAnalogPoint(capControlSubBus.getName() + " kVar Point", UnitOfMeasure.KVAR, pointOffset++, rtuPao, PointUnit.DEFAULT_DECIMAL_PLACES);

        capControlSubBus.getCapControlSubstationBus().setCurrentVoltLoadPointID(voltLoadPoint.getPoint().getPointID());
        capControlSubBus.getCapControlSubstationBus().setCurrentWattLoadPointID(kwPoint.getPoint().getPointID());
        capControlSubBus.getCapControlSubstationBus().setCurrentVarLoadPointID(kvarPoint.getPoint().getPointID());
        
        // 2 special sub bus points, offset 300 and 500.
        createStatusPoint(capControlSubBus.getName() + " Comms Lost", StateGroupUtils.STATEGROUP_TRUEFALSE, 300, subBusPao);
        createStatusPoint(capControlSubBus.getName() + " Bus Disabled", StateGroupUtils.STATEGROUP_TRUEFALSE, 500, subBusPao);
        
        // Note we intentionally do not assign these 2 points, which is very confusing as the UI has 2 points that are not "assigned" but use magic offsets
        // It is not currently known what these assignments actually do
        // capControlSubBus.getCapControlSubstationBus().setDisableBusPointId(busDisablePoint.getPoint().getPointID());

        busService.save(capControlSubBus);
        return pointOffset;
    }
    
    private int attachPointsToFeeder(int pointOffset, PaoIdentifier feederBusPao, PaoIdentifier rtuPao) {
        CapControlFeeder capControlFeeder = feederService.get(feederBusPao.getPaoId());
        
        PointBase voltLoadPoint = createAnalogPoint(capControlFeeder.getName() + " Volt Point", UnitOfMeasure.VOLTS, pointOffset++, rtuPao, PointUnit.DEFAULT_DECIMAL_PLACES);
        PointBase kwPoint = createAnalogPoint(capControlFeeder.getName() + " kWatt Point", UnitOfMeasure.KW, pointOffset++, rtuPao, PointUnit.DEFAULT_DECIMAL_PLACES);
        PointBase kvarPoint = createAnalogPoint(capControlFeeder.getName() + " kVar Point", UnitOfMeasure.KVAR, pointOffset++, rtuPao, PointUnit.DEFAULT_DECIMAL_PLACES);

        capControlFeeder.getCapControlFeeder().setCurrentVoltLoadPointID(voltLoadPoint.getPoint().getPointID());
        capControlFeeder.getCapControlFeeder().setCurrentWattLoadPointID(kwPoint.getPoint().getPointID());
        capControlFeeder.getCapControlFeeder().setCurrentVarLoadPointID(kvarPoint.getPoint().getPointID());

        feederService.save(capControlFeeder);
        return pointOffset;
    }
    
    private int attachPointsToRegulator(int pointOffset, PaoIdentifier regulatorPao, PaoIdentifier rtuPao,
            int subIndex, int setPointRegulatorConfigId, RegulatorVoltageControlMode controlMode) {
        Regulator regulator = voltageRegulatorService.getRegulatorById(regulatorPao.getPaoId());

        if (controlMode == RegulatorVoltageControlMode.SET_POINT) {
            regulator.setConfigId(setPointRegulatorConfigId);
        }

        if (controlMode == RegulatorVoltageControlMode.BOTH) {  // Half of the substations will be SetPoint and half will be Tap
            if ((subIndex % 2) == 0) {
                regulator.setConfigId(setPointRegulatorConfigId);
            }
        }

        PointBase autoRemoteControl = createStatusPoint(regulator.getName() + "-Auto Remote Control", StateGroupUtils.STATEGROUP_TRUEFALSE, pointOffset++, rtuPao);
        PointBase autoBlockEnable = createStatusPoint(regulator.getName() + "-Auto Block Enable", StateGroupUtils.STATEGROUP_TRUEFALSE, pointOffset++, rtuPao);
        PointBase tapUp = createStatusPoint(regulator.getName() + "-Tap Up", StateGroupUtils.STATEGROUPID_CAPBANK, pointOffset++, rtuPao);
        PointBase tapDown = createStatusPoint(regulator.getName() + "-Tap Down", StateGroupUtils.STATEGROUPID_CAPBANK, pointOffset++, rtuPao);
        PointBase terminate = createStatusPoint(regulator.getName() + "-Terminate", StateGroupUtils.STATEGROUPID_CAPBANK, pointOffset++, rtuPao);
        
        PointBase tapPosition = createAnalogPoint(regulator.getName() + "-Tap Position", UnitOfMeasure.TAP, pointOffset++, rtuPao, PointUnit.ZERO_DECIMAL_PLACE);
        PointBase sourceVoltage = createAnalogPoint(regulator.getName() + "-Source Voltage", UnitOfMeasure.VOLTS, pointOffset++, rtuPao, PointUnit.ONE_DECIMAL_PLACE);
        PointBase voltage = createAnalogPoint(regulator.getName() + "-Voltage", UnitOfMeasure.VOLTS, pointOffset++, rtuPao, PointUnit.ONE_DECIMAL_PLACE);
        PointBase keepAlive = createAnalogPoint(regulator.getName() + "-Keep Alive", UnitOfMeasure.UNDEF, pointOffset++, rtuPao, PointUnit.ZERO_DECIMAL_PLACE);
        PointBase forwardSetPoint = createAnalogPoint(regulator.getName() + "-Forward Set Point", UnitOfMeasure.VOLTS, pointOffset++, rtuPao, PointUnit.ONE_DECIMAL_PLACE);
        PointBase forwardBandwidth = createAnalogPoint(regulator.getName() + "-Forward Bandwidth", UnitOfMeasure.VOLTS, pointOffset++, rtuPao, PointUnit.ONE_DECIMAL_PLACE);
        PointBase reverseBandwidth = createAnalogPoint(regulator.getName() + "-Reverse Bandwidth", UnitOfMeasure.VOLTS, pointOffset++, rtuPao, PointUnit.ONE_DECIMAL_PLACE);
        PointBase reverseFlowIndicator = createStatusPoint(regulator.getName() + "-Reverse Flow Indicator", StateGroupUtils.STATEGROUP_TRUEFALSE, pointOffset++, rtuPao);
        PointBase reverseSetPoint =  createAnalogPoint(regulator.getName() + "-Reverse Set Point", UnitOfMeasure.VOLTS, pointOffset++, rtuPao, PointUnit.ONE_DECIMAL_PLACE);
        PointBase controlModePointBase =  createAnalogPoint(regulator.getName() + "-Control Mode", UnitOfMeasure.UNDEF, pointOffset++, rtuPao, PointUnit.ZERO_DECIMAL_PLACE);

        Map<RegulatorPointMapping, Integer> mappings = new ImmutableMap.Builder<RegulatorPointMapping, Integer>()
        .put(RegulatorPointMapping.AUTO_REMOTE_CONTROL, autoRemoteControl.getPoint().getPointID())
        .put(RegulatorPointMapping.AUTO_BLOCK_ENABLE, autoBlockEnable.getPoint().getPointID())
        .put(RegulatorPointMapping.TAP_UP, tapUp.getPoint().getPointID())
        .put(RegulatorPointMapping.TAP_DOWN, tapDown.getPoint().getPointID())
        .put(RegulatorPointMapping.TAP_POSITION, tapPosition.getPoint().getPointID())
        .put(RegulatorPointMapping.TERMINATE, terminate.getPoint().getPointID())
        .put(RegulatorPointMapping.SOURCE_VOLTAGE, sourceVoltage.getPoint().getPointID())
        .put(RegulatorPointMapping.VOLTAGE, voltage.getPoint().getPointID())
        .put(RegulatorPointMapping.KEEP_ALIVE, keepAlive.getPoint().getPointID())
        .put(RegulatorPointMapping.FORWARD_SET_POINT, forwardSetPoint.getPoint().getPointID())
        .put(RegulatorPointMapping.FORWARD_BANDWIDTH, forwardBandwidth.getPoint().getPointID())
        .put(RegulatorPointMapping.REVERSE_BANDWIDTH, reverseBandwidth.getPoint().getPointID())
        .put(RegulatorPointMapping.REVERSE_FLOW_INDICATOR, reverseFlowIndicator.getPoint().getPointID())
        .put(RegulatorPointMapping.REVERSE_SET_POINT, reverseSetPoint.getPoint().getPointID())
        .put(RegulatorPointMapping.CONTROL_MODE, controlModePointBase.getPoint().getPointID())
        .build();
        
        regulator.setMappings(mappings);
        
        try {
            voltageRegulatorService.save(regulator);
        } catch (InvalidDeviceTypeException e) {
            log.warn("caught exception in attachPointsToRegulator", e);
        }
        return pointOffset;
    }
    
    private PointBase createAnalogPoint(String name, UnitOfMeasure unitOfMeasure, int pointOffset, PaoIdentifier pao, int decimalPlaces) {
        PointTemplate pointTemplate = new PointTemplate(name, PointType.Analog, pointOffset, 1, unitOfMeasure.getId(), StateGroupUtils.STATEGROUP_ANALOG, decimalPlaces);
        pointTemplate.setPointArchiveType(PointArchiveType.ON_CHANGE);
        PointBase point = pointCreationService.createPoint(pao, pointTemplate );
        dbPersistentDao.performDBChange(point, TransactionType.INSERT);
        
        return point;
    }
    
    private PointBase createStatusPoint(String name, int stateGroup, int pointOffset, PaoIdentifier pao) {
        PointTemplate pointTemplate = new PointTemplate(name, PointType.Status, pointOffset, 1, 1, stateGroup, PointUnit.DEFAULT_DECIMAL_PLACES);
        pointTemplate.setPointArchiveType(PointArchiveType.ON_CHANGE);
        PointBase point = pointCreationService.createPoint(pao, pointTemplate );
        dbPersistentDao.performDBChange(point, TransactionType.INSERT);
        
        return point;
    }

    // This takes all Voltage inputs on the CBC and attaches them to the bank, and assigns a phase.
    private void assignCBCVoltagePointsToBank(int capbankId, PaoType cbcType) {
        CapBank capbank = capbankService.getCapBank(capbankId);
        List<CCMonitorBankList> unassigned = capbankService.getUnassignedPoints(capbank);
        Integer[] pointIds;

        pointIds = unassigned.stream()
                             .filter(item -> !threePhaseCbcTypes.contains(cbcType) ? (item.getMonitorPoint().getPointName().equalsIgnoreCase("Average Line Voltage")
                                     || item.getMonitorPoint().getPointName().equalsIgnoreCase("Voltage Phase B")
                                     || item.getMonitorPoint().getPointName().equalsIgnoreCase("Voltage Phase C")) :
                                     item.getMonitorPoint().getPointName().equalsIgnoreCase("Average Line Voltage")
                                     || item.getMonitorPoint().getPointName().equalsIgnoreCase("Vb Secondary")
                                     || item.getMonitorPoint().getPointName().equalsIgnoreCase("Vc Secondary"))
                             .map(CCMonitorBankList::getPointId)
                             .toArray(Integer[]::new);

        capbankService.savePoints(capbankId, pointIds);
        capbank = capbankService.getCapBank(capbankId);
        
        List<CCMonitorBankList> ccMonitorBankList = capbank.getCcMonitorBankList();
      
        //There is something odd going on here where it did not load the point name so I based this on display order
        for (CCMonitorBankList item : ccMonitorBankList) {
            if (item.getDisplayOrder() == 2) {
                item.setPhase('B');
            } else if (item.getDisplayOrder() == 3) {
                item.setPhase('C');
            }
        }
  
      capbankService.save(capbank);
    }

    private int createCapControlStrategy(DevCapControl devCapControl) {
        CapControlStrategy strategy = new CapControlStrategy();
        
        //These are currently in UI order
        strategy.setName("Sim IVVC Strategy " + devCapControl.getOffset());
        strategy.setControlMethod(ControlMethod.BUSOPTIMIZED_FEEDER);
        if (devCapControl.isUseIvvcControlType()) {
            strategy.setAlgorithm(ControlAlgorithm.INTEGRATED_VOLT_VAR);
        } else {
            strategy.setAlgorithm(ControlAlgorithm.KVAR);
        }
        strategy.setControlInterval(Minutes.ONE.toStandardSeconds().getSeconds());
        strategy.setMinResponseTime(Minutes.TWO.toStandardSeconds().getSeconds());
        
        Map<DayOfWeek, Boolean> peakDays = strategy.getPeakDays();
        for (Entry<DayOfWeek, Boolean> peakDay : peakDays.entrySet()) {
            if (peakDay.getKey() != DayOfWeek.HOLIDAY) {
                peakDay.setValue(true);
            }
        }
        
        strategy.setPeakStartTime(LocalTime.now().withHourOfDay(8));
        strategy.setPeakStopTime(LocalTime.now().withHourOfDay(20));
        
        Map<TargetSettingType, PeakTargetSetting> targetSettings = strategy.getTargetSettings();
        targetSettings.get(TargetSettingType.UPPER_VOLT_LIMIT).setPeakValue(126);
        targetSettings.get(TargetSettingType.UPPER_VOLT_LIMIT).setOffPeakValue(126);
        targetSettings.get(TargetSettingType.LOWER_VOLT_LIMIT).setPeakValue(118);
        targetSettings.get(TargetSettingType.LOWER_VOLT_LIMIT).setOffPeakValue(116);
        targetSettings.get(TargetSettingType.VOLT_WEIGHT).setPeakValue(5);
        targetSettings.get(TargetSettingType.VOLT_WEIGHT).setOffPeakValue(5);
        targetSettings.get(TargetSettingType.PF_WEIGHT).setPeakValue(2);
        targetSettings.get(TargetSettingType.PF_WEIGHT).setOffPeakValue(2);
        targetSettings.get(TargetSettingType.VOLTAGE_REGULATION_MARGIN).setPeakValue(1.25);
        targetSettings.get(TargetSettingType.VOLTAGE_REGULATION_MARGIN).setOffPeakValue(1.25);
        
        Map<VoltViolationType, VoltageViolationSetting> voltageViolationSettings = strategy.getVoltageViolationSettings();
        voltageViolationSettings.get(VoltViolationType.LOW_VOLTAGE_VIOLATION).setCost(-1);
        
        return strategyService.save(strategy);
    }

    private PaoIdentifier createRegulatorForSubBus(DevCapControl devCapControl, int areaIndex, int subIndex,
                                          int subBusIndex, int feederIndex, int regulatorIndex) {
        String regName = "Sim Regulator " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(regulatorIndex);
        return createCapControlObject(devCapControl, PaoType.PHASE_OPERATED, regName, false, 0);
    }

    private Integer createAndAssignZone(DevCapControl devCapControl, int areaIndex,
                                              int subIndex, PaoIdentifier subBusPao,
                                              int subBusIndex, List<PaoIdentifier> regulatorsForZone, 
                                              List<ZoneAssignmentCapBankRow> bankAssignments, 
                                              Integer parentId, double zonePosition) {
        Zone createdZone = new Zone();
        createdZone.setName("Sim Zone" + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(parentId == null?0:parentId));
        createdZone.setParentId(parentId);
        
        List<RegulatorToZoneMapping> regulatorMappings = new ArrayList<>();
        List<Phase> phases = Phase.getRealPhases();
        for (PaoIdentifier regulator : regulatorsForZone) {
            if(phases.isEmpty())
                phases = Phase.getRealPhases();
            
            RegulatorToZoneMapping regulatorToZoneMapping = new RegulatorToZoneMapping();
            regulatorToZoneMapping.setRegulatorId(regulator.getPaoId());
            regulatorToZoneMapping.setGraphStartPosition(1);
            regulatorToZoneMapping.setPhase(phases.remove(0));
            
            regulatorMappings.add(regulatorToZoneMapping);
        }
        
        createdZone.setRegulators(regulatorMappings );
        
        ZoneThreePhase zone = new ZoneThreePhase(createdZone);
        zone.setSubstationBusId(subBusPao.getPaoId());
        zone.setBankAssignments(bankAssignments);
        zone.setGraphStartPosition(zonePosition);
        zoneService.saveZone(zone);
        return zone.getZoneId();
    }

    private void logCapControlAssignment(String child, PaoIdentifier parent) {
        log.info(child + " assigned to " + parent);
    }
    
    private void createRegulators(DevCapControl devCapControl, int regIndex) {
        for (DevPaoType regType: devCapControl.getRegulatorTypes()) {
            if (regType.isCreate()) {
                String regName = regType.getPaoType().getPaoTypeName() + " " + Integer.toString(regIndex);
                createCapControlObject(devCapControl, regType.getPaoType(), regName, false, 0);
            }
        }
    }
    

    private PaoIdentifier createCapControlObject(DevCapControl devCapControl, PaoType paoType, String name, boolean disabled, int portId) {
        PaoIdentifier paoIdentifier = null;
        try {
            List<LiteYukonPAObject> paos =  paoDao.getLiteYukonPaoByName(name, false);
            if (!paos.isEmpty()) {
                complete++;
                log.info(complete + " / " + total + " CapControl object with name " + name + " already exists. Skipping...");
                devCapControl.incrementFailureCount();
                return paos.get(0).getPaoIdentifier();
            }

            if (paoType.isCbc()) {
                DeviceConfiguration config = deviceConfigurationDao.getDefaultDNPConfiguration();
                paoIdentifier = capControlCreationService.createCbc(paoType, name, disabled, portId, config, null);
            } else {
                paoIdentifier = capControlCreationService.createCapControlObject(paoType, name, false);
            }
            devCapControl.incrementSuccessCount();
            complete++;
            log.info(complete + " / " + total + " CapControl object with name " + name + " created.");
        } catch(RuntimeException e) {
            complete++;
            log.info(complete + " / " + total + " CapControl object with name " + name + " already exists. Skipping");
            devCapControl.incrementFailureCount();
        }
        return paoIdentifier;
    }

    
    private void createAndAssignCBC(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, int feederIndex,
                             int capBankIndex, PaoIdentifier capBankPao) {
        if (capBankPao != null && devCapControl.getCbcType() != null) {
            PaoType paoType = devCapControl.getCbcType().getPaoType();
            String cbcName = "Sim " + paoType.getPaoTypeName() + " " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(capBankIndex);
            PaoIdentifier cbcPao =  createCapControlCBC(devCapControl, paoType, cbcName, false, DevCommChannel.SIM);
            capbankControllerDao.assignController(capBankPao.getPaoId(), cbcPao.getPaoId());
            logCapControlAssignment(cbcName, capBankPao);
            
            if (!threePhaseCbcTypes.contains(paoType)) {
                createAnalogPoint("Voltage Phase B", UnitOfMeasure.VOLTS, 230, cbcPao, PointUnit.DEFAULT_DECIMAL_PLACES);
                createAnalogPoint("Voltage Phase C", UnitOfMeasure.VOLTS, 231, cbcPao, PointUnit.DEFAULT_DECIMAL_PLACES);
            } else {
                createAnalogPoint("Va Secondary", UnitOfMeasure.VOLTS, 39, cbcPao, PointUnit.DEFAULT_DECIMAL_PLACES);
                createAnalogPoint("Vb Secondary", UnitOfMeasure.VOLTS, 40, cbcPao, PointUnit.DEFAULT_DECIMAL_PLACES);
                createAnalogPoint("Vc Secondary", UnitOfMeasure.VOLTS, 41, cbcPao, PointUnit.DEFAULT_DECIMAL_PLACES);
            }
        }
    }
    
    private PaoIdentifier createAndAssignCapBank(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, int feederIndex, PaoIdentifier feederPao,
                             int capBankIndex, List<ZoneAssignmentCapBankRow> bankAssignments, double graphPosition) {
        PaoIdentifier capBankPao = null;
        if(feederPao != null){
            String capBankName = "Sim CapBank " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(capBankIndex);
            capBankPao = createCapControlObject(devCapControl, PaoType.CAPBANK, capBankName, false, 0);
            capbankDao.assignCapbank(feederPao, capBankPao);
            logCapControlAssignment(capBankName, feederPao);
            
            ZoneAssignmentCapBankRow bankAssignment = new ZoneAssignmentCapBankRow();
            bankAssignment.setDevice(capBankName);
            bankAssignment.setGraphPositionOffset(graphPosition);
            bankAssignment.setDistance(graphPosition*1000);
            bankAssignment.setId(capBankPao.getPaoId());
            bankAssignment.setName(capBankName);
            
            bankAssignments.add(bankAssignment);
            
            { // Set cap bank size to (1800, 1200, 900, 600, 600, 600....)
                CapBank capBank = capbankService.getCapBank(capBankPao.getPaoId());
                capBank.getCapBank().setBankSizeCustom(capBankIndex == 0 ? 1800 : capBankIndex == 1 ? 1200 : capBankIndex == 2 ? 900 : 600);
                capbankService.save(capBank);
            }
            
        }
        return capBankPao;
    }

    private PaoIdentifier createAndAssignFeeder(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, PaoIdentifier subBusPao, int feederIndex) {
        PaoIdentifier feederPao = null;
        if (subBusPao != null) {
            String feederName = "Sim Feeder " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex);
            feederPao = createCapControlObject(devCapControl, PaoType.CAP_CONTROL_FEEDER, feederName, false, 0);
            feederDao.assignFeeder(subBusPao, feederPao);
            logCapControlAssignment(feederName, subBusPao);
        }
        return feederPao;
    }

    private  PaoIdentifier createAndAssignSubstationBus(DevCapControl devCapControl, int areaIndex, int subIndex, PaoIdentifier substationPao , int subBusIndex, int strategyId) {
        PaoIdentifier subBusPao = null;
        if (substationPao != null) {
            String subBusName = "Sim Substation Bus " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex);
            subBusPao = createCapControlObject(devCapControl, PaoType.CAP_CONTROL_SUBBUS, subBusName, false, 0);
            substationBusDao.assignSubstationBus(substationPao, subBusPao);
            logCapControlAssignment(subBusName, substationPao);
            
            // Assign a strategy (probably IVVC) to this sub bus
            final Season season = new Season("Default", -1);
            seasonScheduleDao.saveSeasonStrategyAssigment(subBusPao.getPaoId(), ImmutableMap.of(season, strategyId), -1);
        }
        return subBusPao;
    }

    private PaoIdentifier createSubstation(DevCapControl devCapControl, int areaIndex, PaoIdentifier areaPao, int subIndex) {
        PaoIdentifier substationPao = null;
        if (areaPao != null) {
            String subName = "Sim Substation " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex);
            substationPao = createCapControlObject(devCapControl, PaoType.CAP_CONTROL_SUBSTATION, subName, false, 0);
            substationDao.assignSubstation(areaPao, substationPao);
            logCapControlAssignment(subName, areaPao);
        }
        return substationPao;
    }

    private PaoIdentifier createArea(DevCapControl devCapControl, int areaIndex) {
        String areaName = "Sim Area " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex);
        return createCapControlObject(devCapControl, PaoType.CAP_CONTROL_AREA, areaName, false, 0);
    }
    
    private PaoIdentifier createCapControlCBC(DevCapControl devCapControl, PaoType paoType, String name, boolean disabled, DevCommChannel commChannel) {
        List<LiteYukonPAObject> commChannels = paoDao.getLiteYukonPaoByName(commChannel.getName(), false);
        if (commChannels.size() != 1) {
            throw new RuntimeException("Couldn't find comm channel " + commChannel.getName());
        }
        int portId = commChannels.get(0).getPaoIdentifier().getPaoId();
        return createCapControlObject(devCapControl, paoType, name, disabled, portId);
    }

    private int createDeviceConfigurationForSetPointRegulator(DevCapControl devCapControl) {
        int setPointRegulatorConfigId = deviceConfigurationService.saveConfigurationBase(null, "Sim Regulator Configuration" + devCapControl.getOffset(), StringUtils.EMPTY);

        deviceConfigurationDao.getSupportedTypesForConfiguration(setPointRegulatorConfigId);
        deviceConfigurationDao.addSupportedDeviceTypes(setPointRegulatorConfigId,
            Sets.newHashSet(PaoType.PHASE_OPERATED, PaoType.GANG_OPERATED, PaoType.LOAD_TAP_CHANGER));
        List<DeviceConfigCategoryItem> categoryItems = new ArrayList<>();
        DeviceConfigCategoryItem controlModeCategoryItem = new DeviceConfigCategoryItem(null, "voltageControlMode", "SET_POINT");
        DeviceConfigCategoryItem tapCategoryItem = new DeviceConfigCategoryItem(null, "voltageChangePerTap", ".75");
        categoryItems.add(controlModeCategoryItem);
        categoryItems.add(tapCategoryItem);
        DeviceConfigCategory configCategory = new DeviceConfigCategory(null, CategoryType.REGULATOR_CATEGORY.value(),
            "Sim Regulator category" + devCapControl.getOffset(), StringUtils.EMPTY, categoryItems);
        int categoryId = deviceConfigService.saveCategory(configCategory);
        deviceConfigurationService.changeCategoryAssignment(setPointRegulatorConfigId, categoryId, CategoryType.REGULATOR_CATEGORY);
        
        List<DeviceConfigCategoryItem> heartbeatCategoryItems = new ArrayList<>();
        DeviceConfigCategoryItem heartbeatMode = new DeviceConfigCategoryItem(null, "RegulatorHeartbeatMode", "NONE");
        heartbeatCategoryItems.add(heartbeatMode);
        DeviceConfigCategory heartbeatConfigCategory = new DeviceConfigCategory(null, CategoryType.REGULATOR_HEARTBEAT.value(),
                "Sim Regulator heartbeat" + devCapControl.getOffset(), StringUtils.EMPTY, heartbeatCategoryItems);
        int regulatorHearbeatCategoryId = deviceConfigService.saveCategory(heartbeatConfigCategory);
        deviceConfigurationService.changeCategoryAssignment(setPointRegulatorConfigId, regulatorHearbeatCategoryId,
            CategoryType.REGULATOR_HEARTBEAT);

        return setPointRegulatorConfigId;

    }
}

package com.cannontech.web.dev.database.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.capcontrol.creation.service.CapControlCreationService;
import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneAssignmentCapBankRow;
import com.cannontech.capcontrol.model.ZoneGang;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.model.Season;
import com.cannontech.development.model.DevCommChannel;
import com.cannontech.development.model.DevPaoType;
import com.cannontech.development.service.impl.DevObjectCreationBase;
import com.cannontech.web.capcontrol.service.StrategyService;
import com.cannontech.web.dev.database.objects.DevCapControl;
import com.cannontech.web.dev.database.service.DevCapControlCreationService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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

    private static final ReentrantLock _lock = new ReentrantLock();
    private static int complete;
    private static int total;
    
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
        for (int areaIndex = 0; areaIndex  < devCapControl.getNumAreas(); areaIndex++) { // Areas
            PaoIdentifier areaPao = createArea(devCapControl, areaIndex);
            for (int subIndex = 0; subIndex < devCapControl.getNumSubs(); subIndex++) { // Substations
                
                PaoIdentifier substationPao = createSubstation(devCapControl, areaIndex, areaPao , subIndex);
               
                for (int subBusIndex = 0; subBusIndex < devCapControl.getNumSubBuses(); subBusIndex++) { // Substations Buses
                    
                    PaoIdentifier subBusPao =  createAndAssignSubstationBus(devCapControl, areaIndex, subIndex, substationPao , subBusIndex, strategyId);
                    
                    List<ZoneAssignmentCapBankRow> bankAssignments = new ArrayList<>();
                    
                    for (int feederIndex = 0; feederIndex < devCapControl.getNumFeeders(); feederIndex++) { // Feeders
                        PaoIdentifier feederPao = createAndAssignFeeder(devCapControl, areaIndex, subIndex, subBusIndex, subBusPao, feederIndex);
                        for (int capBankIndex = 0; capBankIndex < devCapControl.getNumCapBanks(); capBankIndex++) { // CapBanks & CBCs
                            PaoIdentifier capBankPao = createAndAssignCapBank(devCapControl, areaIndex, subIndex, subBusIndex, feederIndex, feederPao, capBankIndex, bankAssignments);
                            createAndAssignCBC(devCapControl, areaIndex, subIndex, subBusIndex, feederIndex, capBankIndex, capBankPao);
                        }
                    }

                    PaoIdentifier regulatorPao = createRegulatorForSubBus(devCapControl, areaIndex, subIndex, subBusIndex);
                    createAndAssignZone(devCapControl, areaIndex, subIndex, subBusPao, subBusIndex, regulatorPao.getPaoId(), bankAssignments);

                }
            }
        }
        
        for (int regIndex = 0; regIndex < devCapControl.getNumRegulators(); regIndex++) { // Additional Regulators
            createRegulators(devCapControl, regIndex);
        }
    }

    private int createCapControlStrategy(DevCapControl devCapControl) {
        CapControlStrategy strategy = new CapControlStrategy();
        
        //These are currently in UI order
        strategy.setName("Sim IVVC Strategy " + devCapControl.getOffset());
        strategy.setControlMethod(ControlMethod.SUBSTATION_BUS);
        strategy.setAlgorithm(ControlAlgorithm.INTEGRATED_VOLT_VAR);
        
        return strategyService.save(strategy);
    }

    private PaoIdentifier createRegulatorForSubBus(DevCapControl devCapControl, int areaIndex, int subIndex,
                                          int subBusIndex) {
        String regName = "Sim Regulator " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex);
        return createCapControlObject(devCapControl, PaoType.GANG_OPERATED, regName, false, 0);
    }

    private void createAndAssignZone(DevCapControl devCapControl, int areaIndex,
                                              int subIndex, PaoIdentifier subBusPao,
                                              int subBusIndex, int regulatorId, 
                                              List<ZoneAssignmentCapBankRow> bankAssignments) {
        Zone createdZone = new Zone();
        createdZone.setName("Sim Zone" + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex));
        RegulatorToZoneMapping regulatorToZoneMapping = new RegulatorToZoneMapping();
        regulatorToZoneMapping.setRegulatorId(regulatorId);
        regulatorToZoneMapping.setGraphStartPosition(1);
        regulatorToZoneMapping.setPhase(Phase.ALL);
        
        createdZone.setRegulators(ImmutableList.of(regulatorToZoneMapping));
        
        ZoneGang zone = new ZoneGang(createdZone);
        zone.setSubstationBusId(subBusPao.getPaoId());
        zone.setBankAssignments(bankAssignments);
        zoneService.saveZone(zone);
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
                paoIdentifier = capControlCreationService.createCbc(paoType, name, disabled, portId, config);
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
            String cbcName = paoType.getPaoTypeName() + " " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(capBankIndex);
            PaoIdentifier cbcPao =  createCapControlCBC(devCapControl, paoType, cbcName, false, DevCommChannel.SIM);
            capbankControllerDao.assignController(capBankPao.getPaoId(), cbcPao.getPaoId());
            logCapControlAssignment(cbcName, capBankPao);
        }
    }
    
    private PaoIdentifier createAndAssignCapBank(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, int feederIndex, PaoIdentifier feederPao,
                             int capBankIndex, List<ZoneAssignmentCapBankRow> bankAssignments) {
        PaoIdentifier capBankPao = null;
        if(feederPao != null){
            String capBankName = "Sim CapBank " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(capBankIndex);
            capBankPao = createCapControlObject(devCapControl, PaoType.CAPBANK, capBankName, false, 0);
            capbankDao.assignCapbank(feederPao, capBankPao);
            logCapControlAssignment(capBankName, feederPao);
            
            ZoneAssignmentCapBankRow bankAssignment = new ZoneAssignmentCapBankRow();
            bankAssignment.setDevice(capBankName);
            bankAssignment.setGraphPositionOffset((capBankIndex+1)*4.5);
            bankAssignment.setDistance((capBankIndex+1)*5000);
            bankAssignment.setId(capBankPao.getPaoId());
            bankAssignment.setName(capBankName);
            
            bankAssignments.add(bankAssignment);
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
}

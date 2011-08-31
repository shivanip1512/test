package com.cannontech.web.support.development.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.cbc.service.CapControlCreationService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.web.support.development.database.objects.DevCapControl;
import com.cannontech.web.support.development.database.objects.DevCommChannel;
import com.cannontech.web.support.development.database.objects.DevPaoType;

public class DevCapControlCreationService extends DevObjectCreationBase {
    private CapControlCreationService capControlCreationService;
    private StrategyDao strategyDao;
    
    @Override
    protected void createAll() {
        createCapControl(devDbSetupTask.getDevCapControl());
    }
    
    @Override
    protected void logFinalExecutionDetails() {
        log.info("CC:");
    }
    
    private void createCapControl(DevCapControl devCapControl) {
        log.info("Creating (and assigning) Cap Control Objects ...");
        for (int areaIndex = 0; areaIndex  < devCapControl.getNumAreas(); areaIndex++) { // Areas
            String areaName = createArea(devCapControl, areaIndex);
            for (int subIndex = 0; subIndex < devCapControl.getNumSubs(); subIndex++) { // Substations
                String subName = createSubstation(devCapControl, areaIndex, areaName, subIndex);
                for (int subBusIndex = 0; subBusIndex < devCapControl.getNumSubBuses(); subBusIndex++) { // Substations Buses
                    String subBusName = createAndAssignSubstationBus(devCapControl, areaIndex, subIndex, subName, subBusIndex);
                    for (int feederIndex = 0; feederIndex < devCapControl.getNumFeeders(); feederIndex++) { // Feeders
                        String feederName = createAndAssignFeeder(devCapControl, areaIndex, subIndex, subBusIndex, subBusName, feederIndex);
                        for (int capBankIndex = 0; capBankIndex < devCapControl.getNumCapBanks(); capBankIndex++) { // CapBanks & CBCs
                            String capBankName = createAndAssignCapBank(devCapControl, areaIndex, subIndex, subBusIndex, feederIndex, feederName, capBankIndex);
                            createAndAssignCBC(devCapControl, areaIndex, subIndex, subBusIndex, feederIndex, feederName, capBankIndex, capBankName);
                        }
                    }
                }
            }
        }
        
        for (int regIndex = 0; regIndex < devCapControl.getNumRegulators(); regIndex++) { //Regulators
            createRegulators(devCapControl, regIndex);
        }
    }

    private void logCapControlAssignment(String child, String parent) {
        log.info(child + " assigned to " + parent);
    }
    
    private void createRegulators(DevCapControl devCapControl, int regIndex) {
        for (DevPaoType regType: devCapControl.getRegulatorTypes()) {
            if (regType.isCreate()) {
                String regName = regType.getPaoType().getPaoTypeName() + " " + Integer.toString(regIndex);
                createCapControlObject(devCapControl, regType.getPaoType().getDeviceTypeId(), regName, false, 0);
            }
        }
    }
    
    private void createCapControlObject(DevCapControl devCapControl, int type, String name, boolean disabled, int portId) {
        try {
            LiteYukonPAObject litePao = getPaoByName(name);
            if (litePao != null) {
                log.info("CapControl object with name " + name + " already exists. Skipping");
                devCapControl.incrementFailureCount();
                return;
            }
            capControlCreationService.create(type, name, disabled);
            devCapControl.incrementSuccessCount();
            log.info("CapControl object with name " + name + " created.");
        } catch(RuntimeException e) {
            log.info("CapControl object with name " + name + " already exists. Skipping");
            devCapControl.incrementFailureCount();
        }
    }

    private void createAndAssignCBC(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, int feederIndex, String feederName,
                             int capBankIndex, String capBankName) {
        DevPaoType cbcType = devCapControl.getCbcType();
        if (cbcType == null) {
            return;
        }
        String cbcName = cbcType.getPaoType().getPaoTypeName() + " " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(capBankIndex);
        createCapControlCBC(devCapControl, cbcType.getPaoType().getDeviceTypeId(), cbcName, false, DevCommChannel.SIM);
        int cbcPaoId = getPaoIdByName(cbcName);
        capControlCreationService.assignController(cbcPaoId, cbcType.getPaoType(), capBankName);
        logCapControlAssignment(cbcName, capBankName);
    }

    private String createAndAssignCapBank(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, int feederIndex, String feederName,
                             int capBankIndex) {
        String capBankName = "CapBank " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(capBankIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_CAPBANK, capBankName, false, 0);
        int capBankPaoId = getPaoIdByName(capBankName);
        capControlCreationService.assignCapbank(capBankPaoId, feederName);
        logCapControlAssignment(capBankName, feederName);
        return capBankName;
    }

    private String createAndAssignFeeder(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, String subBusName, int feederIndex) {
        String feederName = "Feeder " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_FEEDER, feederName, false, 0);
        int feederPaoId = getPaoIdByName(feederName);
        capControlCreationService.assignFeeder(feederPaoId, subBusName);
        logCapControlAssignment(feederName, subBusName);
        return feederName;
    }

    private String createAndAssignSubstationBus(DevCapControl devCapControl, int areaIndex, int subIndex, String subName, int subBusIndex) {
        String subBusName = "Substation Bus " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_SUBBUS, subBusName, false, 0);
        int subBusPaoId = getPaoIdByName(subBusName);
        capControlCreationService.assignSubstationBus(subBusPaoId, subName);
        logCapControlAssignment(subBusName, subName);
        return subBusName;
    }

    private String createSubstation(DevCapControl devCapControl, int areaIndex, String areaName, int subIndex) {
        String subName = "Substation " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_SUBSTATION, subName, false, 0);
        int subPaoId = getPaoIdByName(subName);
        capControlCreationService.assignSubstation(subPaoId, areaName);
        logCapControlAssignment(subName, areaName);
        return subName;
    }

    private String createArea(DevCapControl devCapControl, int areaIndex) {
        String areaName = "Area " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex);
        createCapControlObject(devCapControl, CapControlTypes.CAP_CONTROL_AREA, areaName, false, 0);
        return areaName;
    }
    
    private void createCapControlCBC(DevCapControl devCapControl, int type, String name, boolean disabled, DevCommChannel commChannel) {
        checkIsCancelled();
        List<LiteYukonPAObject> commChannels = paoDao.getLiteYukonPaoByName(commChannel.getName(), false);
        if (commChannels.size() != 1) {
            throw new RuntimeException("Couldn't find comm channel " + commChannel.getName());
        }
        int portId = commChannels.get(0).getPaoIdentifier().getPaoId();
        createCapControlObject(devCapControl, type, name, disabled, portId);
    }
    
    private void createCapControlSchedule(DevCapControl devCapControl, int type, String name, boolean disabled) {
        List<PAOSchedule> allPaoScheduleNames = paoScheduleDao.getAllPaoScheduleNames();
        for (PAOSchedule paoSchedule : allPaoScheduleNames) {
            if (paoSchedule.getScheduleName().equalsIgnoreCase(name)) {
                log.info("CapControl object with name " + name + " already exists. Skipping");
                return;
            }
        }
        createCapControlObject(devCapControl, type, name, disabled, 0);
    }
    
    private void createCapControlStrategy(DevCapControl devCapControl, int type, String name, boolean disabled) {
        List<LiteCapControlStrategy> allLiteStrategies = strategyDao.getAllLiteStrategies();
        for (LiteCapControlStrategy liteStrategy : allLiteStrategies) {
            if (liteStrategy.getStrategyName().equalsIgnoreCase(name)) {
                log.info("CapControl object with name " + name + " already exists. Skipping");
                return;
            }
        }
        createCapControlObject(devCapControl, type, name, false, 0);
    }

    @Autowired
    public void setCapControlCreationService(CapControlCreationService capControlCreationService) {
        this.capControlCreationService = capControlCreationService;
    }
    @Autowired
    public void setStrategyDao(StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }
}

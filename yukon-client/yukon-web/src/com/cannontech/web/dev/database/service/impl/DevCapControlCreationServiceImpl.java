package com.cannontech.web.dev.database.service.impl;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.creation.service.CapControlCreationService;
import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.web.dev.database.objects.DevCapControl;
import com.cannontech.web.dev.database.objects.DevCommChannel;
import com.cannontech.web.dev.database.objects.DevPaoType;
import com.cannontech.web.dev.database.service.DevCapControlCreationService;

public class DevCapControlCreationServiceImpl extends DevObjectCreationBase implements DevCapControlCreationService {
    @Autowired private CapControlCreationService capControlCreationService;
    @Autowired private SubstationDao substationDao;
    @Autowired private CapbankDao capbankDao;
    @Autowired private FeederDao feederDao;
    @Autowired private SubstationBusDao substationBusDao;
    @Autowired private CapbankControllerDao capbankControllerDao;
    @Autowired private PaoScheduleDao paoScheduleDao;
    @Autowired private StrategyDao strategyDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;

    private static final ReentrantLock _lock = new ReentrantLock();
    private static int complete;
    private static int total;
    
    @Override
    public boolean isRunning() {
        return _lock.isLocked();
    }

    @Override
    @Transactional
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
                createCapControlObject(devCapControl, regType.getPaoType(), regName, false, 0);
//                complete++;
//                log.info(complete + " / " + total);
            }
        }
    }
    
    private void createCapControlObject(DevCapControl devCapControl, PaoType paoType, String name, boolean disabled, int portId) {
        try {
            LiteYukonPAObject litePao = getPaoByName(name);
            if (litePao != null) {
                complete++;
                log.info(complete + " / " + total + " CapControl object with name " + name + " already exists. Skipping...");
                devCapControl.incrementFailureCount();
                return;
            }

            if (paoType.isCbc()) {
                DeviceConfiguration config = deviceConfigurationDao.getDefaultDNPConfiguration();
                capControlCreationService.createCbc(paoType, name, disabled, portId, config);
            } else {
                capControlCreationService.createCapControlObject(paoType, name, false);
            }
            devCapControl.incrementSuccessCount();
            complete++;
            log.info(complete + " / " + total + " CapControl object with name " + name + " created.");
        } catch(RuntimeException e) {
            complete++;
            log.info(complete + " / " + total + " CapControl object with name " + name + " already exists. Skipping");
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
        createCapControlCBC(devCapControl, cbcType.getPaoType(), cbcName, false, DevCommChannel.SIM);
        int cbcPaoId = getPaoIdByName(cbcName);
        capbankControllerDao.assignController(cbcPaoId, capBankName);
        logCapControlAssignment(cbcName, capBankName);
    }

    private String createAndAssignCapBank(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, int feederIndex, String feederName,
                             int capBankIndex) {
        String capBankName = "CapBank " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex) + Integer.toString(capBankIndex);
        createCapControlObject(devCapControl, PaoType.CAPBANK, capBankName, false, 0);
        int capBankPaoId = getPaoIdByName(capBankName);
        capbankDao.assignCapbank(capBankPaoId, feederName);
        logCapControlAssignment(capBankName, feederName);
//        complete++;
//        log.info(complete + " / " + total);
        return capBankName;
    }

    private String createAndAssignFeeder(DevCapControl devCapControl, int areaIndex, int subIndex, int subBusIndex, String subBusName, int feederIndex) {
        String feederName = "Feeder " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex) + Integer.toString(feederIndex);
        createCapControlObject(devCapControl, PaoType.CAP_CONTROL_FEEDER, feederName, false, 0);
        int feederPaoId = getPaoIdByName(feederName);
        feederDao.assignFeeder(feederPaoId, subBusName);
        logCapControlAssignment(feederName, subBusName);
//        complete++;
//        log.info(complete + " / " + total);
        return feederName;
    }

    private String createAndAssignSubstationBus(DevCapControl devCapControl, int areaIndex, int subIndex, String subName, int subBusIndex) {
        String subBusName = "Substation Bus " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex) + Integer.toString(subBusIndex);
        createCapControlObject(devCapControl, PaoType.CAP_CONTROL_SUBBUS, subBusName, false, 0);
        int subBusPaoId = getPaoIdByName(subBusName);
        substationBusDao.assignSubstationBus(subBusPaoId, subName);
        logCapControlAssignment(subBusName, subName);
//        complete++;
//        log.info(complete + " / " + total);
        return subBusName;
    }

    private String createSubstation(DevCapControl devCapControl, int areaIndex, String areaName, int subIndex) {
        String subName = "Substation " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex) + Integer.toString(subIndex);
        createCapControlObject(devCapControl, PaoType.CAP_CONTROL_SUBSTATION, subName, false, 0);
        int subPaoId = getPaoIdByName(subName);
        substationDao.assignSubstation(subPaoId, areaName);
        logCapControlAssignment(subName, areaName);
//        complete++;
//        log.info(complete + " / " + total);
        return subName;
    }

    private String createArea(DevCapControl devCapControl, int areaIndex) {
        String areaName = "Area " + devCapControl.getOffset() + "_" + Integer.toString(areaIndex);
        createCapControlObject(devCapControl, PaoType.CAP_CONTROL_AREA, areaName, false, 0);
//        complete++;
//        log.info(complete + " / " + total);
        return areaName;
    }
    
    private void createCapControlCBC(DevCapControl devCapControl, PaoType paoType, String name, boolean disabled, DevCommChannel commChannel) {
        List<LiteYukonPAObject> commChannels = paoDao.getLiteYukonPaoByName(commChannel.getName(), false);
        if (commChannels.size() != 1) {
            throw new RuntimeException("Couldn't find comm channel " + commChannel.getName());
        }
        int portId = commChannels.get(0).getPaoIdentifier().getPaoId();
        createCapControlObject(devCapControl, paoType, name, disabled, portId);
//        complete++;
//        log.info(complete + " / " + total);
    }
    
    private void createCapControlSchedule(DevCapControl devCapControl, int type, String name, boolean disabled) {
        List<PAOSchedule> allPaoScheduleNames = paoScheduleDao.getAllPaoScheduleNames();
        for (PAOSchedule paoSchedule : allPaoScheduleNames) {
            if (paoSchedule.getScheduleName().equalsIgnoreCase(name)) {
                log.info("CapControl object with name " + name + " already exists. Skipping");
                return;
            }
        }
        
        paoScheduleDao.add(name, disabled);
    }
    
    private void createCapControlStrategy(DevCapControl devCapControl, int type, String name, boolean disabled) {
        List<LiteCapControlStrategy> allLiteStrategies = strategyDao.getAllLiteStrategies();
        for (LiteCapControlStrategy liteStrategy : allLiteStrategies) {
            if (liteStrategy.getStrategyName().equalsIgnoreCase(name)) {
                log.info("CapControl object with name " + name + " already exists. Skipping");
                return;
            }
        }
        
        strategyDao.add(name);
    }

}

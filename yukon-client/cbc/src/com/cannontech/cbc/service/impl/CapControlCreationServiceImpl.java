package com.cannontech.cbc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.VoltageRegulatorDefaults;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.dao.providers.fields.VoltageRegulatorFields;
import com.cannontech.cbc.dao.AreaDao;
import com.cannontech.cbc.dao.CapbankControllerDao;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.cbc.dao.FeederDao;
import com.cannontech.cbc.dao.SubstationBusDao;
import com.cannontech.cbc.dao.SubstationDao;
import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankAdditional;
import com.cannontech.cbc.model.CapbankController;
import com.cannontech.cbc.model.Feeder;
import com.cannontech.cbc.model.SpecialArea;
import com.cannontech.cbc.model.Substation;
import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.cbc.service.CapControlCreationService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.PaoCreationService;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.ClassToInstanceMap;

public class CapControlCreationServiceImpl implements CapControlCreationService {

	private CapbankControllerDao capbankControllerDao;
	private CapbankDao capbankDao;
	private FeederDao feederDao;
	private SubstationBusDao substationBusDao;
	private SubstationDao substationDao;
	private AreaDao areaDao;
	private PaoDao paoDao;
	private DBPersistentDao dbPersistantDao;
    private PaoScheduleDao paoScheduleDao;
    private StrategyDao strategyDao;
    private PaoCreationService paoCreationService;
	
	@Override
	@Transactional
	public int create(int type, String name, boolean disabled, int portId) {
        int id = -1;
        switch(type) {

            case CapControlTypes.CAP_CONTROL_SPECIAL_AREA :
                SpecialArea spArea = new SpecialArea();
                spArea.setName(name);
                spArea.setDisabled(disabled);
                createSpecialArea(spArea);
                id = spArea.getId();
                break;
                
            case CapControlTypes.CAP_CONTROL_AREA :
                Area area = new Area();
                area.setName(name);
                area.setDisabled(disabled);
                createArea(area);
                id = area.getId();
                break;
                
            case CapControlTypes.CAP_CONTROL_SUBSTATION :
                Substation substation = new Substation();
                substation.setName(name);
                substation.setDisabled(disabled);
                createSubstation(substation);
                id = substation.getId();
                break;
    
            case CapControlTypes.CAP_CONTROL_SUBBUS :
                SubstationBus bus = new SubstationBus();
                bus.setName(name);
                bus.setDisabled(disabled);
                createSubstationBus(bus);
                id = bus.getId();
                break;
    
            case CapControlTypes.CAP_CONTROL_FEEDER :
                Feeder feeder = new Feeder();
                feeder.setName(name);
                feeder.setDisabled(disabled);
                createFeeder(feeder);
                id = feeder.getId();
                break;
    
            case CapControlTypes.CAP_CONTROL_CAPBANK :
                Capbank capbank = new Capbank();
                capbank.setName(name);
                capbank.setDisabled(disabled);
                capbank.setCapbankAdditional(new CapbankAdditional());
                createCapbank(capbank);
                id = capbank.getId();
                break;
    
            case CapControlTypes.CAP_CONTROL_SCHEDULE :
                id = createPAOSchedule(name, disabled);
                break;
                
            case CapControlTypes.CAP_CONTROL_STRATEGY :
                id = createStrategy(name);
                break;
                
            case CapControlTypes.CAP_CONTROL_LTC:
            case CapControlTypes.GANG_OPERATED_REGULATOR:
            case CapControlTypes.PHASE_OPERATED_REGULATOR:
                id = createRegulator(name, disabled, type);
                break;
                
            default : // must be a cbc
                CapbankController controller = new CapbankController();
                PaoType deviceType = PaoType.getForId(type);
                
                if (CapbankController.isValidCbc(deviceType)) {
                    controller.setScanGroup(0);
                    controller.setScanType(DeviceScanRate.TYPE_INTEGRITY);
                    controller.setIntervalRate(300);
                    controller.setAlternateRate(300);
                    controller.setName(name);
                    controller.setType(deviceType);
                    
                    if (!CapbankController.isOneWayCbc(deviceType)) {
                        controller.setPortId(portId);
                    }
                    
                    controller.setDisabled(disabled);
                    createController(controller);
                    id = controller.getId();
                } else {
                    throw new UnsupportedOperationException("Import of " + name + " failed. Unknown CBC Type: " + deviceType.getDbString());
                }
                
                break;
                
        }
        
        return id;
    }
	
	private int createRegulator(String name, boolean disabled, int type) {
	    VoltageRegulatorFields voltageRegulatorFields = new VoltageRegulatorFields();
	    voltageRegulatorFields.setKeepAliveTimer(VoltageRegulatorDefaults.KEEP_ALIVE_TIMER);
	    voltageRegulatorFields.setKeepAliveConfig(VoltageRegulatorDefaults.KEEP_ALIVE_CONFIG);

	    YukonPaObjectFields yukonPaoFields = new YukonPaObjectFields(name);
	    ClassToInstanceMap<PaoTemplatePart> paoFields = paoCreationService.createFieldMap();
	    paoFields.put(YukonPaObjectFields.class, yukonPaoFields);
	    paoFields.put(VoltageRegulatorFields.class, voltageRegulatorFields);

	    PaoTemplate paoTemplate = new PaoTemplate(PaoType.getForId(type), paoFields);
	    PaoIdentifier paoIdentifier = paoCreationService.createPao(paoTemplate);

        return paoIdentifier.getPaoId();
    }
	
    @Override
    @Transactional
	public void createArea(Area area) {
		areaDao.add(area);
		
		//Send DB add message
        sendCapcontrolDBChangeMessage(area.getId(),DbChangeType.ADD,CapControlType.AREA);
	}
	
	@Override
	@Transactional
    public void createSpecialArea(SpecialArea specialArea) {
        areaDao.addSpecialArea(specialArea);
        
        //Send DB add message
        sendCapcontrolDBChangeMessage(specialArea.getId(),DbChangeType.ADD,CapControlType.SPECIAL_AREA);
    }
	
	@Override
	@Transactional
	public void createSubstation(Substation substation) {
		substationDao.add(substation);

		//Send DB add message
		sendCapcontrolDBChangeMessage(substation.getId(),DbChangeType.ADD,CapControlType.SUBSTATION);
	}
	
	@Override
	@Transactional
    public int createPAOSchedule(String name, boolean disabled) {
        return paoScheduleDao.add(name, disabled);
    }
	
	@Override
	@Transactional
    public int createStrategy(String name) {
        return strategyDao.add(name);
    }
	
	@Override
	@Transactional
	public boolean assignSubstation(int substationId, int areaId) {
		boolean ret = substationDao.assignSubstation(areaId, substationId);
		
		if (ret) {
			sendCapcontrolDBChangeMessage(substationId,DbChangeType.UPDATE,CapControlType.SUBSTATION);
			sendCapcontrolDBChangeMessage(areaId,DbChangeType.UPDATE,CapControlType.AREA);
		}
		return ret;
	}

	@Override
	public boolean assignSubstation(int substationId, String areaName) { 
		int id = getPaoIdByName(areaName);
		if (id == -1) {
			return false;
		}
		
		return assignSubstation(substationId, id);
	}

	@Override
	@Transactional
	public boolean unassignSubstation(int substationId) {
		return substationDao.unassignSubstation(substationId);
	}
	
	@Override
	@Transactional
	public void createCapbank(Capbank bank) {
		capbankDao.add(bank);
		
		sendCapcontrolDBChangeMessage(bank.getId(),DbChangeType.ADD,CapControlType.CAPBANK);
	}
	
	@Override
	@Transactional
	public boolean assignCapbank(int bankId, int feederId) {
		boolean ret = capbankDao.assignCapbank(feederId, bankId);
		
		if (ret) {
			sendCapcontrolDBChangeMessage(bankId,DbChangeType.UPDATE,CapControlType.CAPBANK);
			sendCapcontrolDBChangeMessage(feederId,DbChangeType.UPDATE,CapControlType.FEEDER);
		}
		
		return ret;
		
	}

	@Override
	public boolean assignCapbank(int bankId, String feederName) {
		int id = getPaoIdByName(feederName);
		if (id == -1) {
			return false;
		}
		
		return assignCapbank(bankId, id);
	}

	@Override
	@Transactional
	public boolean unassignCapbank(int capbankId) {
		return capbankDao.unassignCapbank(capbankId);
	}

	@Override
	@Transactional
	public void createFeeder(Feeder feeder) {
		feederDao.add(feeder);
		
		sendCapcontrolDBChangeMessage(feeder.getId(),DbChangeType.ADD,CapControlType.FEEDER);
	}
	
	@Override
	@Transactional
	public boolean assignFeeder(int feederId, int subBusId) {
		boolean ret = feederDao.assignFeeder(subBusId, feederId);
		if (ret) {
			sendCapcontrolDBChangeMessage(feederId,DbChangeType.UPDATE,CapControlType.FEEDER);
			sendCapcontrolDBChangeMessage(subBusId,DbChangeType.UPDATE,CapControlType.SUBBUS);
		}
		
		return ret;
	}
	
	@Override
	public boolean assignFeeder(int feederId, String subBusName) {
		int id = getPaoIdByName(subBusName);
		if (id == -1) {
			return false;
		}
		
		return assignFeeder(feederId, id);
	}

	@Override
	@Transactional
	public boolean unassignFeeder(int feederId) {
		return feederDao.unassignFeeder(feederId);
	}
	
	@Override
	@Transactional
	public void createSubstationBus(SubstationBus subBus) {
		substationBusDao.add(subBus);
		
		sendCapcontrolDBChangeMessage(subBus.getId(),DbChangeType.ADD,CapControlType.SUBBUS);
	}
	
	@Override
	@Transactional
	public boolean assignSubstationBus(int subBusId, int substationId) {
		boolean ret = substationBusDao.assignSubstationBus(substationId, subBusId);
		
		if (ret) {
			sendCapcontrolDBChangeMessage(subBusId,DbChangeType.UPDATE, CapControlType.SUBBUS);
			sendCapcontrolDBChangeMessage(substationId, DbChangeType.UPDATE,CapControlType.SUBSTATION);
		}
		
		return ret; 
	}
	
	@Override
	public boolean assignSubstationBus(int subBusId, String substationName) {
		int id = getPaoIdByName(substationName);
		if (id == -1) {
			return false;
		}
		
		return assignSubstationBus(subBusId, id);
	}

	@Override
	@Transactional
	public boolean unassignSubstationBus(int subBusId) {
		return substationBusDao.unassignSubstationBus(subBusId);
	}
	
	@Override
	@Transactional
	public void createController(CapbankController controller) {
		capbankControllerDao.add(controller);

		String type = controller.getType().getDbString();
        sendDeviceDBChangeMessage(controller.getId(),DbChangeType.ADD,type);
	}
	
	@Override
	@Transactional
	public boolean createControllerFromTemplate(String template, CapbankController controller) {
		//Dao must set type in controller.
		boolean success = capbankControllerDao.createControllerFromTemplate(template, controller);

		if (success) {
			String devType = controller.getType().getDbString();
			sendDeviceDBChangeMessage(controller.getId(),DbChangeType.ADD,devType);
		}
		
		return success;
	}
	
	@Override
	@Transactional
	public boolean assignController(CapbankController controller, int capbankId) {
		boolean ret = capbankControllerDao.assignController(capbankId, controller.getId());
		PaoType deviceType = controller.getType();
		
		if (ret) {
		    sendDeviceDBChangeMessage(controller.getId(),DbChangeType.UPDATE, deviceType.getDbString());
		    sendCapcontrolDBChangeMessage(capbankId, DbChangeType.UPDATE,CapControlType.CAPBANK.getDbValue());
		}
		
		return ret; 
	}

	@Override
	public boolean assignController(CapbankController controller, String capBankName) {
		int id = getPaoIdByName(capBankName);
		if(id == -1) {
			return false;
		}
		
		return assignController(controller, id);
	}
	
	@Override
	@Transactional
	public boolean unassignController(int controllerId) {
		return capbankControllerDao.unassignController(controllerId);
	}
	
	private LiteYukonPAObject getPaoByName(String paoName) {
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(paoName, false);
		
		if (paos.size() != 1) {
			return null;
		}
		LiteYukonPAObject litePao = paos.get(0);
		
		return litePao;
	}
	
	private int getPaoIdByName(String paoName) {
		LiteYukonPAObject litePao = getPaoByName(paoName);
		
		if (litePao == null) {
			return -1;
		}
		
		return litePao.getYukonID();
	}
	
    private void sendDeviceDBChangeMessage(int paoId, DbChangeType dbChangeType, String type) {
        DBChangeMsg msg = new DBChangeMsg(paoId, DBChangeMsg.CHANGE_PAO_DB,
                PAOGroups.STRING_CAT_DEVICE, type, dbChangeType); 
        dbPersistantDao.processDBChange(msg);
    }

	
	private void sendCapcontrolDBChangeMessage(int paoId, DbChangeType dbChangeType, String type) {
		DBChangeMsg msg = new DBChangeMsg(paoId, DBChangeMsg.CHANGE_PAO_DB,
				PAOGroups.STRING_CAT_CAPCONTROL, type, dbChangeType);	
		dbPersistantDao.processDBChange(msg);
	}
	
	private void sendCapcontrolDBChangeMessage(int paoId, DbChangeType dbChangeType, CapControlType type) {
		sendCapcontrolDBChangeMessage(paoId, dbChangeType, type.getDbValue());
	}
	
	@Autowired
	public void setCapbankControllerDao(CapbankControllerDao cbcDao) {
		this.capbankControllerDao = cbcDao;
	}
	@Autowired
	public void setCapbankDao(CapbankDao capbankDao) {
		this.capbankDao = capbankDao;
	}
	@Autowired
	public void setFeederDao(FeederDao feederDao) {
		this.feederDao = feederDao;
	}
	@Autowired
	public void setSubstationBusDao(SubstationBusDao substationBusDao) {
		this.substationBusDao = substationBusDao;
	}
	@Autowired
	public void setSubstationDao(SubstationDao substationDao) {
		this.substationDao = substationDao;
	}
	@Autowired
	public void setAreaDao(AreaDao areaDao) {
		this.areaDao = areaDao;
	}
	@Autowired
	public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
	@Autowired
	public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
		this.dbPersistantDao = dbPersistantDao;
	}
	
	@Autowired
    public void setPaoScheduleDao(PaoScheduleDao paoScheduleDao) {
        this.paoScheduleDao = paoScheduleDao;
    }
	
	@Autowired
    public void setStrategyDao(StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }
	
	@Autowired
	public void setPaoCreationService(PaoCreationService paoCreationService) {
        this.paoCreationService = paoCreationService;
    }
}

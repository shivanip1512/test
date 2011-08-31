package com.cannontech.cbc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.dao.providers.fields.AreaFields;
import com.cannontech.capcontrol.dao.providers.fields.CapBankFields;
import com.cannontech.capcontrol.dao.providers.fields.CapbankAdditionalFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceAddressFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceCbcFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceDirectCommSettingsFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceScanRateFields;
import com.cannontech.capcontrol.dao.providers.fields.DeviceWindowFields;
import com.cannontech.capcontrol.dao.providers.fields.FeederFields;
import com.cannontech.capcontrol.dao.providers.fields.SpecialAreaFields;
import com.cannontech.capcontrol.dao.providers.fields.SubstationBusFields;
import com.cannontech.capcontrol.dao.providers.fields.SubstationFields;
import com.cannontech.capcontrol.dao.providers.fields.VoltageRegulatorFields;
import com.cannontech.capcontrol.model.CapbankController;
import com.cannontech.cbc.service.CapControlCreationService;
import com.cannontech.common.pao.PaoCategory;
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
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class CapControlCreationServiceImpl implements CapControlCreationService {

	private CapbankControllerDao capbankControllerDao;
	private CapbankDao capbankDao;
	private FeederDao feederDao;
	private SubstationBusDao substationBusDao;
	private SubstationDao substationDao;
	private PaoDao paoDao;
	private DBPersistentDao dbPersistantDao;
    private PaoScheduleDao paoScheduleDao;
    private StrategyDao strategyDao;
    private PaoCreationService paoCreationService;
	
    @Override
    @Transactional
    public int createCbc(PaoType paoType, String name, boolean disabled, int portId) {
    	ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
    	
		// Create and set-up the YukonPaObjectFields
		paoFields.put(YukonPaObjectFields.class, new YukonPaObjectFields(name));
		paoFields.put(DeviceFields.class, new DeviceFields());
		
		// Set up the fields based on the CBC type.
		switch (paoType) {
			case CBC_7020:
			case CBC_7022:
			case CBC_7023:
			case CBC_7024:
			case CBC_8020:
			case CBC_8024:
			case CBC_DNP:
			case DNP_CBC_6510:
				paoFields.put(DeviceScanRateFields.class, new DeviceScanRateFields());
				paoFields.put(DeviceWindowFields.class, new DeviceWindowFields());
				paoFields.put(DeviceDirectCommSettingsFields.class, new DeviceDirectCommSettingsFields(portId));
				
				if (paoType != PaoType.DNP_CBC_6510) {
					paoFields.put(DeviceCbcFields.class, new DeviceCbcFields());
				}
				
				paoFields.put(DeviceAddressFields.class, new DeviceAddressFields());
				
				break;

			case CBC_7010:
			case CBC_7011:
			case CBC_7012:
			case CBC_EXPRESSCOM:
			case CBC_FP_2800:
			case CAPBANKCONTROLLER:
				paoFields.put(DeviceCbcFields.class, new DeviceCbcFields());
				
				break;
				
			default:
				throw new IllegalArgumentException("Import of " + name + " failed. Unknown CBC Type: " + paoType.getDbString());
		}
		
        PaoTemplate paoTemplate = new PaoTemplate(paoType, paoFields);
        
        return paoCreationService.createPao(paoTemplate).getPaoId();
    }
    
	private int create(PaoType paoType, String name) {
        int id = -1;
        switch(paoType) {

            case CAP_CONTROL_SPECIAL_AREA :
            case CAP_CONTROL_AREA :
            case CAP_CONTROL_SUBSTATION :
            case CAP_CONTROL_SUBBUS :
            case CAP_CONTROL_FEEDER :
            case CAPBANK :
            	id = createHierarchyObject(paoType, name);
                break;
                
            case LOAD_TAP_CHANGER:
            case GANG_OPERATED:
            case PHASE_OPERATED:
                id = createRegulator(name, paoType);
                break;
                
            default:
                throw new UnsupportedOperationException("Import of " + name + " failed. Unknown CBC Type: " + paoType.getDbString());
        }
        
        return id;
    }
	
	@Override
	@Transactional
	public int create(int type, String name, boolean disabled) {
		if (type == CapControlTypes.CAP_CONTROL_SCHEDULE) {
			return createPAOSchedule(name, disabled);
		} else if( type == CapControlTypes.CAP_CONTROL_STRATEGY) {
			return createStrategy(name);
		} else {
			return create(PaoType.getForId(type), name);
		}
	}
	
	private int createHierarchyObject(PaoType paoType, String name) {
		ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
		
		// Create and set-up the YukonPaObjectFields
		paoFields.put(YukonPaObjectFields.class, new YukonPaObjectFields(name));
		
		switch (paoType) {
		case CAP_CONTROL_AREA:
			paoFields.put(AreaFields.class, new AreaFields());
			break;
		case CAP_CONTROL_SPECIAL_AREA:
			paoFields.put(SpecialAreaFields.class, new SpecialAreaFields());
			break;
		case CAP_CONTROL_SUBSTATION:
			paoFields.put(SubstationFields.class, new SubstationFields());
			break;
		case CAP_CONTROL_SUBBUS:
			paoFields.put(SubstationBusFields.class, new SubstationBusFields());
			break;
		case CAP_CONTROL_FEEDER:
			paoFields.put(FeederFields.class, new FeederFields());
			break;
		case CAPBANK:
			paoFields.put(CapBankFields.class, new CapBankFields());
			paoFields.put(DeviceFields.class, new DeviceFields());
			paoFields.put(CapbankAdditionalFields.class, new CapbankAdditionalFields());
			break;	
		default:
			throw new IllegalArgumentException("Import of hierarchy object " + name + " failed. Unknown type: " + paoType.getDbString());
		}
		
        PaoTemplate paoTemplate = new PaoTemplate(paoType, paoFields);
        
        return paoCreationService.createPao(paoTemplate).getPaoId();
	}
	
	@Override
    @Transactional
	public int createRegulator(String name, PaoType paoType) {
		YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(name);
	    VoltageRegulatorFields voltageRegulatorFields = new VoltageRegulatorFields(0,0);

	    ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
	    paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);
	    paoFields.put(VoltageRegulatorFields.class, voltageRegulatorFields);

	    PaoTemplate paoTemplate = new PaoTemplate(paoType, paoFields);
        
        return paoCreationService.createPao(paoTemplate).getPaoId();
    }
	
	@Override
    @Transactional
	public int createArea(String name) {
		return createHierarchyObject(PaoType.CAP_CONTROL_AREA, name);
	}
	
	@Override
	@Transactional
    public int createSpecialArea(String name) {
		return createHierarchyObject(PaoType.CAP_CONTROL_SPECIAL_AREA, name);
    }
	
	@Override
	@Transactional
	public int createSubstation(String name) {
		return createHierarchyObject(PaoType.CAP_CONTROL_SUBSTATION, name);
	}
	
	@Override
	@Transactional
	public int createSubstationBus(String name) {
		return createHierarchyObject(PaoType.CAP_CONTROL_SUBBUS, name);
	}
	
	@Override
	@Transactional
	public int createFeeder(String name) {
		return createHierarchyObject(PaoType.CAP_CONTROL_FEEDER, name);
	}
	
	@Override
	@Transactional
	public int createCapbank(String name) {
		return createHierarchyObject(PaoType.CAPBANK, name);
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
		throw new UnsupportedOperationException("CBC creation should be handled by the PaoCreationService!");
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
		return assignController(controller.getId(), controller.getType(), capbankId);
	}
	
	@Override
	@Transactional
	public boolean assignController(int controllerId, PaoType controllerType, int capbankId) {
	    boolean ret = capbankControllerDao.assignController(capbankId, controllerId);
	    
	    if (ret) {
	        sendDeviceDBChangeMessage(controllerId,DbChangeType.UPDATE, controllerType.getDbString());
	        sendCapcontrolDBChangeMessage(capbankId, DbChangeType.UPDATE,CapControlType.CAPBANK.getDbValue());
	    }
	    
	    return ret; 
	}

	@Override
	@Transactional
	public boolean assignController(int controllerId, PaoType controllerType, String capBankName) {
        int id = getPaoIdByName(capBankName);
        if(id == -1) {
            return false;
        }
        return assignController(controllerId, controllerType, id);
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
                						  PaoCategory.DEVICE.getDbString(), type, dbChangeType); 
        dbPersistantDao.processDBChange(msg);
    }

	
	private void sendCapcontrolDBChangeMessage(int paoId, DbChangeType dbChangeType, String type) {
		DBChangeMsg msg = new DBChangeMsg(paoId, DBChangeMsg.CHANGE_PAO_DB, 
										  PaoCategory.CAPCONTROL.getDbString(), type, dbChangeType);	
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

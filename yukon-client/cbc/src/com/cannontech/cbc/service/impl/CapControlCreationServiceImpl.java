package com.cannontech.cbc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.cbc.dao.AreaDao;
import com.cannontech.cbc.dao.CapbankControllerDao;
import com.cannontech.cbc.dao.SubstationBusDao;
import com.cannontech.cbc.dao.SubstationDao;
import com.cannontech.cbc.dao.FeederDao;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.Capbank;
import com.cannontech.cbc.model.CapbankController;
import com.cannontech.cbc.model.Feeder;
import com.cannontech.cbc.model.Substation;
import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.cbc.service.CapControlCreationService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.common.device.DeviceType;

public class CapControlCreationServiceImpl implements CapControlCreationService {

	private CapbankControllerDao capbankControllerDao;
	private CapbankDao capbankDao;
	private FeederDao feederDao;
	private SubstationBusDao substationBusDao;
	private SubstationDao substationDao;
	private AreaDao areaDao;
	private PaoDao paoDao;
	private DBPersistentDao dbPersistantDao;
	
	@Override
	public boolean createArea(Area area) {
		boolean success = areaDao.add(area);
		
		if (success) {
			//Send DB add message
			sendDBChangeMessage(area.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.AREA);
		}

		return success;
	}
	
	@Override
	public boolean createSubstation(Substation substation) {
		boolean success = substationDao.add(substation);

		if (success) {
			//Send DB add message
			sendDBChangeMessage(substation.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.SUBSTATION);
		}

		return success;
	}
	
	@Override
	public boolean assignSubstation(int substationId, int areaId) {
		boolean ret = substationDao.assignSubstation(areaId, substationId);
		
		if (ret) {
			sendDBChangeMessage(substationId,DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.SUBSTATION);
			sendDBChangeMessage(areaId,DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.AREA);
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
	public boolean unassignSubstation(int substationId) {
		return substationDao.unassignSubstation(substationId);
	}
	
	@Override
	public boolean createCapbank(Capbank bank) {
		boolean success = capbankDao.add(bank);
		
		if (success) {
			sendDBChangeMessage(bank.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.CAPBANK);
		}
		
		return success;
	}
	
	@Override
	public boolean assignCapbank(int bankId, int feederId) {
		boolean ret = capbankDao.assignCapbank(feederId, bankId);
		
		if (ret) {
			sendDBChangeMessage(bankId,DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.CAPBANK);
			sendDBChangeMessage(feederId,DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.FEEDER);
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
	public boolean unassignCapbank(int capbankId) {
		return capbankDao.unassignCapbank(capbankId);
	}

	@Override
	public boolean createFeeder(Feeder feeder) {
		boolean success = feederDao.add(feeder);
		
		if (!success) {
			sendDBChangeMessage(feeder.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.FEEDER);
		}
		
		return success;
	}
	
	@Override
	public boolean assignFeeder(int feederId, int subBusId) {
		boolean ret = feederDao.assignFeeder(subBusId, feederId);
		if (ret) {
			sendDBChangeMessage(feederId,DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.FEEDER);
			sendDBChangeMessage(subBusId,DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.SUBBUS);
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
	public boolean unassignFeeder(int feederId) {
		return feederDao.unassignFeeder(feederId);
	}
	
	@Override
	public boolean createSubstationBus(SubstationBus subBus) {
		boolean success = substationBusDao.add(subBus);
		
		if (success) {
			sendDBChangeMessage(subBus.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.SUBBUS);
		}

		return success;
	}
	
	@Override
	public boolean assignSubstationBus(int subBusId, int substationId) {
		boolean ret = substationBusDao.assignSubstationBus(substationId, subBusId);
		
		if (ret) {
			sendDBChangeMessage(subBusId,DBChangeMsg.CHANGE_TYPE_UPDATE, CapControlType.SUBBUS);
			sendDBChangeMessage(substationId, DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.SUBSTATION);
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
	public boolean unassignSubstationBus(int subBusId) {
		return substationBusDao.unassignSubstationBus(subBusId);
	}
	
	@Override
	public boolean createController(CapbankController controller) {
		boolean success = capbankControllerDao.add(controller);

		if (success) {
			String type = DeviceType.getForId(controller.getType()).name();
			sendDBChangeMessage(controller.getId(),DBChangeMsg.CHANGE_TYPE_ADD,type);
		}
		
		return success;
	}
	
	@Override
	public boolean createControllerFromTemplate(String template, CapbankController controller) {
		//Dao must set type in controller.
		boolean success = capbankControllerDao.createControllerFromTemplate(template, controller);

		if (success) {
			String devType = DeviceType.getForId(controller.getType()).name();
			sendDBChangeMessage(controller.getId(),DBChangeMsg.CHANGE_TYPE_ADD,devType);
		}
		
		return success;
	}
	
	@Override
	public boolean assignController(CapbankController controller, int capbankId) {
		boolean ret = capbankControllerDao.assignController(capbankId, controller.getId());
		DeviceType deviceType = DeviceType.getForId(controller.getType());
		
		if (ret) {
			sendDBChangeMessage(controller.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE, deviceType.getPaoTypeName());
			sendDBChangeMessage(capbankId, DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.CAPBANK);
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
	
	private void sendDBChangeMessage(int paoId, int changeType, String type) {
		DBChangeMsg msg = new DBChangeMsg(paoId, DBChangeMsg.CHANGE_PAO_DB,
				PAOGroups.STRING_CAT_CAPCONTROL, type, changeType);	
		dbPersistantDao.processDBChange(msg);
	}
	
	private void sendDBChangeMessage(int paoId, int changeType, CapControlType type) {
		sendDBChangeMessage(paoId,changeType,type.getDisplayValue());
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
	
}

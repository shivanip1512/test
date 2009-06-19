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
		
		if (!success) {
			//Attempt to update instead, maybe it is already in the database.
			int id = getPaoIdByName(area.getName());
			if (id == -1) {
				//Not found in the Database.
				return false;
			} 
			area.setId(id);
			
			if (success = areaDao.update(area)) {
				sendDBChangeMessage(area.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.AREA);
			}
		} else {
			//Send DB add message
			sendDBChangeMessage(area.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.AREA);
		}

		return success;
	}
	
	@Override
	public boolean createSubstation(Substation substation) {
		boolean success = substationDao.add(substation);

		if (!success) {
			//Attempt to update instead, maybe it is already in the database.
			int id = getPaoIdByName(substation.getName());
			if (id == -1) {
				//Not found in the Database.
				return false;
			} 
			substation.setId(id);
			
			if (success = substationDao.update(substation)) {
				sendDBChangeMessage(substation.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.SUBSTATION);
			}
		} else {
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
		if(id == -1) {
			return false;
		}
		
		return assignSubstation(substationId, id);
	}

	@Override
	public boolean createCapbank(Capbank bank) {
		boolean success = capbankDao.add(bank);
		
		if (!success) {
			//Attempt to update instead, maybe it is already in the database.
			int id = getPaoIdByName(bank.getName());
			if (id == -1) {
				//Not found in the Database.
				return false;
			} 
			bank.setId(id);
			
			if (success = capbankDao.update(bank)) {
				sendDBChangeMessage(bank.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.CAPBANK);
			}
		} else {
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
		if(id == -1) {
			return false;
		}
		
		return assignCapbank(bankId, id);
	}

	@Override
	public boolean createFeeder(Feeder feeder) {
		boolean success = feederDao.add(feeder);
		
		if (!success) {
			//Attempt to update instead, maybe it is already in the database.
			int id = getPaoIdByName(feeder.getName());
			if (id == -1) {
				//Not found in the Database.
				return false;
			} 
			feeder.setId(id);
			
			if (success = feederDao.update(feeder)) {
				sendDBChangeMessage(feeder.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.FEEDER);
			}
		} else {
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
		if(id == -1) {
			return false;
		}
		
		return assignFeeder(feederId, id);
	}

	@Override
	public boolean createSubstationBus(SubstationBus subBus) {
		boolean success = substationBusDao.add(subBus);
		
		if (!success) {
			//Attempt to update instead, maybe it is already in the database.
			int id = getPaoIdByName(subBus.getName());
			if (id == -1) {
				//Not found in the Database.
				return false;
			} 
			subBus.setId(id);
			
			if(success = substationBusDao.update(subBus)) {
				sendDBChangeMessage(subBus.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.SUBBUS);
			}
		} else {
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
		if(id == -1) {
			return false;
		}
		
		return assignSubstationBus(subBusId, id);
	}

	@Override
	public boolean createController(CapbankController controller) {
		boolean success = capbankControllerDao.add(controller);
		String type = PAOGroups.getPAOTypeString(controller.getType());
		
		if (!success) {
			//Attempt to update instead, maybe it is already in the database.
			int id = getPaoIdByName(controller.getName());
			if (id == -1) {
				//Not found in the Database.
				return false;
			} 
			controller.setId(id);
			
			if (success = capbankControllerDao.update(controller)) {
				sendDBChangeMessage(controller.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,type);
			}
		} else {
			sendDBChangeMessage(controller.getId(),DBChangeMsg.CHANGE_TYPE_ADD,type);
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
	
	private int getPaoIdByName(String paoName) {
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(paoName, false);
		
		if (paos.size() != 1) {
			return -1;
		}
		LiteYukonPAObject litePao = paos.get(0);
		
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

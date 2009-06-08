package com.cannontech.cbc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cbc.dao.AreaDao;
import com.cannontech.cbc.dao.SubstationBusDao;
import com.cannontech.cbc.dao.SubstationDao;
import com.cannontech.cbc.dao.FeederDao;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.Capbank;
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

public class CapControlCreationServiceImpl implements CapControlCreationService {

	private CapbankDao capbankDao;
	private FeederDao feederDao;
	private SubstationBusDao substationBusDao;
	private SubstationDao substationDao;
	private AreaDao areaDao;
	private PaoDao paoDao;
	private DBPersistentDao dbPersistantDao;
	
	@Override
	public boolean createArea(Area area) {
		DBChangeMsg dbChange = null;
		boolean success = areaDao.add(area);
		
		if (!success) {
			//Attempt to update instead, maybe it is already in the database.
			if (success = areaDao.update(area)) {
				dbChange = generateDBChangeMessage(area.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.AREA);
			}
		} else {
			//Send DB add message
			dbChange = generateDBChangeMessage(area.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.AREA);
		}
		
		dbPersistantDao.processDBChange(dbChange);
		
		return success;
	}
	
	@Override
	public boolean createSubstation(Substation substation) {
		DBChangeMsg dbChange = null;
		boolean success = substationDao.add(substation);
		
		if (!success) {
			if (success = substationDao.update(substation)) {
				dbChange = generateDBChangeMessage(substation.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.SUBSTATION);
			}
		} else {
			//Send DB add message
			dbChange = generateDBChangeMessage(substation.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.SUBSTATION);
		}
		
		dbPersistantDao.processDBChange(dbChange);
		
		return success;
	}
	
	@Override
	public boolean assignSubstation(int substationId, int areaId) {
		boolean ret = substationDao.assignSubstation(areaId, substationId);
		
		if (ret) {
			dbPersistantDao.processDBChange(generateDBChangeMessage(substationId,
											DBChangeMsg.CHANGE_TYPE_UPDATE, 
											CapControlType.SUBSTATION));
			dbPersistantDao.processDBChange(generateDBChangeMessage(areaId,
											DBChangeMsg.CHANGE_TYPE_UPDATE, 
											CapControlType.AREA));
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
/*	
	@Override
	public boolean createCapbank(Capbank bank) {
		DBChangeMsg dbChange = null;
		boolean success = capbankDao.add(bank);
		
		if (!success) {
			if (success = capbankDao.update(bank)) {
				dbChange = generateDBChangeMessage(bank.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.CAPBANK);
			}
		} else {
			dbChange = generateDBChangeMessage(bank.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.CAPBANK);
		}
		
		dbPersistantDao.processDBChange(dbChange);
		
		return success;
	}
	
	@Override
	public boolean assignCapbank(int bankId, int feederId) {
		boolean ret = capbankDao.assignCapbank(feederId, bankId);
		
		if (ret) {
			dbPersistantDao.processDBChange(generateDBChangeMessage(bankId,
											DBChangeMsg.CHANGE_TYPE_UPDATE, 
											CapControlType.CAPBANK));
			dbPersistantDao.processDBChange(generateDBChangeMessage(feederId,
											DBChangeMsg.CHANGE_TYPE_UPDATE, 
											CapControlType.FEEDER));
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
*/

	@Override
	public boolean createFeeder(Feeder feeder) {
		DBChangeMsg dbChange = null;
		boolean success = feederDao.add(feeder);
		
		if (!success) {
			if (success = feederDao.update(feeder)) {
				dbChange = generateDBChangeMessage(feeder.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.FEEDER);
			}
		} else {
			dbChange = generateDBChangeMessage(feeder.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.FEEDER);
		}
		
		dbPersistantDao.processDBChange(dbChange);
		
		return success;
	}
	
	@Override
	public boolean assignFeeder(int feederId, int subBusId) {
		boolean ret = feederDao.assignFeeder(subBusId, feederId);
		if (ret) {
			dbPersistantDao.processDBChange(generateDBChangeMessage(feederId,
											DBChangeMsg.CHANGE_TYPE_UPDATE, 
											CapControlType.FEEDER));
			dbPersistantDao.processDBChange(generateDBChangeMessage(subBusId,
											DBChangeMsg.CHANGE_TYPE_UPDATE, 
											CapControlType.SUBBUS));
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
		DBChangeMsg dbChange = null;
		boolean success = substationBusDao.add(subBus);
		
		if (!success) {
			if(success = substationBusDao.update(subBus)) {
				dbChange = generateDBChangeMessage(subBus.getId(),DBChangeMsg.CHANGE_TYPE_UPDATE,CapControlType.SUBBUS);
			}
		} else {
			dbChange = generateDBChangeMessage(subBus.getId(),DBChangeMsg.CHANGE_TYPE_ADD,CapControlType.SUBBUS);
		}
		
		dbPersistantDao.processDBChange(dbChange);
		
		return success;
	}
	
	@Override
	public boolean assignSubstationBus(int subBusId, int substationId) {
		boolean ret = substationBusDao.assignSubstationBus(substationId, subBusId);
		
		if (ret) {
			dbPersistantDao.processDBChange(generateDBChangeMessage(subBusId,
					DBChangeMsg.CHANGE_TYPE_UPDATE, CapControlType.SUBBUS));
			dbPersistantDao.processDBChange(generateDBChangeMessage(
					substationId, DBChangeMsg.CHANGE_TYPE_UPDATE,
					CapControlType.SUBSTATION));
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

	private int getPaoIdByName(String paoName) {
		List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(paoName, false);
		
		if (paos.size() != 1) {
			return -1;
		}
		LiteYukonPAObject litePao = paos.get(0);
		
		return litePao.getYukonID();
	}
	
	private DBChangeMsg generateDBChangeMessage(int paoId, int changeType, CapControlType type) {
		DBChangeMsg msg = new DBChangeMsg(paoId, DBChangeMsg.CHANGE_PAO_DB,
				PAOGroups.STRING_CAT_CAPCONTROL, type.getDisplayValue(),
				changeType);
		
		return msg;
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

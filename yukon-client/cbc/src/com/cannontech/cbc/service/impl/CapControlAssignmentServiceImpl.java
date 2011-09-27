package com.cannontech.cbc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.cbc.service.CapControlAssignmentService;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class CapControlAssignmentServiceImpl implements CapControlAssignmentService {

	private CapbankControllerDao capbankControllerDao;
	private CapbankDao capbankDao;
	private FeederDao feederDao;
	private SubstationBusDao substationBusDao;
	private SubstationDao substationDao;
	private DBPersistentDao dbPersistantDao;
	private PaoDao paoDao;
	
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
		YukonPao pao = paoDao.findYukonPao(areaName, PaoType.CAP_CONTROL_AREA);
		
		return (pao == null) ? false : assignSubstation(substationId, pao.getPaoIdentifier().getPaoId());
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
		YukonPao pao = paoDao.findYukonPao(feederName, PaoType.CAP_CONTROL_FEEDER);
		
		return (pao == null) ? false : assignCapbank(bankId, pao.getPaoIdentifier().getPaoId());
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
		YukonPao pao = paoDao.findYukonPao(subBusName, PaoType.CAP_CONTROL_SUBBUS);
		
		return (pao == null) ? false : assignFeeder(feederId, pao.getPaoIdentifier().getPaoId());
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
		YukonPao pao = paoDao.findYukonPao(substationName, PaoType.CAP_CONTROL_SUBSTATION);
		
		return (pao == null) ? false : assignSubstationBus(subBusId, pao.getPaoIdentifier().getPaoId());
	}

	@Override
	@Transactional
	public boolean unassignSubstationBus(int subBusId) {
		return substationBusDao.unassignSubstationBus(subBusId);
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
		YukonPao pao = paoDao.findYukonPao(capBankName, PaoType.CAPBANK);
		
        return (pao == null) ? false : assignController(controllerId, controllerType, pao.getPaoIdentifier().getPaoId());
	}
	
	@Override
	@Transactional
	public boolean unassignController(int controllerId) {
		return capbankControllerDao.unassignController(controllerId);
	}
	
	private void sendCapcontrolDBChangeMessage(int paoId, DbChangeType dbChangeType, CapControlType type) {
		sendCapcontrolDBChangeMessage(paoId, dbChangeType, type.getDbValue());
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
}

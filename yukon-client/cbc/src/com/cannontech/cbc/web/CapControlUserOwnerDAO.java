package com.cannontech.cbc.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;

public class CapControlUserOwnerDAO implements CapControlDAO {

	private CapControlDAO _ccDao;
	private LiteYukonUser _user;
	
	public CapControlUserOwnerDAO(CapControlDAO dao, LiteYukonUser user) {
		super();
		_ccDao = dao;
		_user = user;
	}

	public CapControlUserOwnerDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SubBus[] getAllSubBuses() {
	//check 

		return _ccDao.getAllSubBuses();
	}

	public List getAreaNames() {
		List names = _ccDao.getAreaNames();
		return names;
	}

	public CapBankDevice getCapBankDevice(Integer capBankDeviceID) {
		return _ccDao.getCapBankDevice(capBankDeviceID);
	}

	public CapBankDevice[] getCapBanksByArea(String area) {
		return _ccDao.getCapBanksByArea(area);
	}

	public CapBankDevice[] getCapBanksByFeeder(Integer feederID) {
		return _ccDao.getCapBanksByFeeder(feederID);
	}

	public CapBankDevice[] getCapBanksBySub(Integer subBusID) {
		return _ccDao.getCapBanksBySub(subBusID);
	}

	public LiteState getCapBankState(int rawState) {
		return _ccDao.getCapBankState(rawState);
	}

	public StreamableCapObject getCapControlPAO(Integer paoID) {
		return _ccDao.getCapControlPAO(paoID);
	}

	public Feeder getFeeder(Integer feederID) {
		return _ccDao.getFeeder(feederID);
	}

	public Feeder[] getFeedersByArea(String area) {
		return _ccDao.getFeedersByArea(area);
	}

	public Feeder[] getFeedersBySub(Integer subBusID) {
		return _ccDao.getFeedersBySub(subBusID);
	}

	public LiteWrapper[] getOrphanedCapBanks() {
		return _ccDao.getOrphanedCapBanks();
	}

	public LiteWrapper[] getOrphanedCBCs() {
		return _ccDao.getOrphanedCBCs();
	}

	public LiteWrapper[] getOrphanedFeeders() {
		return _ccDao.getOrphanedFeeders();
	}

	public String getParentNames(int id) {
		return _ccDao.getParentNames(id);
	}

	public int getParentSubBusID(int childID) {
		return _ccDao.getParentSubBusID(childID);
	}

	public SubBus getSubBus(Integer subID) {
		return _ccDao.getSubBus(subID);
	}

	public SubBus[] getSubsByArea(String area) {
		SubBus[] subs =  _ccDao.getSubsByArea(area);
		SubBus[] retArray = new SubBus[subs.length];
		List subsAllowedToView = new ArrayList(10);
		for (int i=0; i < subs.length; i++) {
			SubBus sub = subs[i];
			if (DaoFactory.getAuthDao().userHasAccessPAO(_user, sub.getCcId().intValue()))
				subsAllowedToView.add(sub);
		}
		return ((SubBus[])subsAllowedToView.toArray(retArray));
	}

	public boolean isCapBank(int id) {
		return _ccDao.isCapBank(id);
	}

	public boolean isFeeder(int id) {
		return _ccDao.isFeeder(id);
	}

	public boolean isSubBus(int id) {
		return _ccDao.isSubBus(id);
	}
    
    public HashMap getAreaStateMap() {
        return _ccDao.getAreaStateMap();
    }



}

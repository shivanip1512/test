package com.cannontech.cbc.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.filters.CacheFilter;
import com.cannontech.cbc.web.CBCWebUpdatedObjectMap;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

/** Filter is going by the area right now, the other's will be as normal.
 * 
 * @author tspar
 *
 */

public class FilterCapControlCacheImpl implements CapControlCache {
	private CapControlCache cache;
	private CacheFilter filter;
	
	public FilterCapControlCacheImpl() {

    }
	
	public void setFilter (CacheFilter filter) {
		this.filter = filter;
	}
	
    public void setCache(CapControlCache cache) {
        this.cache = cache;
    }
    
	public SubBus[] getAllSubBuses() {
		SubBus[] aList = cache.getAllSubBuses();
		SubBus[] retList = new SubBus[ aList.length ];
		
		int j = 0;
		for( int i = 0; i < aList.length ; i++ ){
			SubBus a = aList[i];
			int id = cache.getParentAreaID(a.getCcId());
			CBCArea area = cache.getArea(id);
			if ( filter.valid(area) )
				retList[j] = a;
		}
		return retList;
	}

	public HashMap getAreaStateMap() {
		// TODO Auto-generated method stub
		return cache.getAreaStateMap();
	}

	public CBCArea getCBCArea(int id) {
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return area;
		else
			return null;
	}

	public CBCSpecialArea getCBCSpecialArea(int id) {
		CBCSpecialArea area = cache.getCBCSpecialArea(id);
		if ( filter.valid(area) )
			return area;
		else
			return null;
	}

	public CapBankDevice getCapBankDevice(Integer capBankDeviceID) {
		CapBankDevice cap = cache.getCapBankDevice(capBankDeviceID);
		int id = cache.getParentAreaID(cap.getCcId());
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cap;
		else
			return null;
	}

	public LiteState getCapBankState(int rawState) {
		// TODO Auto-generated method stub
		return cache.getCapBankState(rawState);
	}

	public List<CapBankDevice> getCapBanksByArea(Integer areaID) {
		CBCArea area = cache.getCBCArea(areaID);
		if ( filter.valid(area) )
			return cache.getCapBanksByArea(areaID);
		else
			return null;
	}

	public CapBankDevice[] getCapBanksByFeeder(Integer feederID) {
		int id = cache.getParentAreaID(feederID);
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cache.getCapBanksByFeeder(feederID);
		else
			return null;
	}

    public String getSubBusNameForFeeder(Feeder fdr){
        int id = cache.getParentAreaID(fdr.getCcId());
        CBCArea area = cache.getCBCArea(id);
        if ( filter.valid(area) )
            return cache.getSubBusNameForFeeder(fdr);
        else
            return "";

    }
	public List<CapBankDevice> getCapBanksBySubBus(Integer subBusID) {
		int id = cache.getParentAreaID(subBusID);
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cache.getCapBanksBySubBus(subBusID);
		else
			return null;
	}

	public List<CapBankDevice> getCapBanksBySubStation(SubStation sub) {
		int id = cache.getParentAreaID(sub.getCcId());
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cache.getCapBanksBySubStation(sub);
		else
			return new ArrayList<CapBankDevice>();
	}

	public StreamableCapObject getCapControlPAO(Integer paoID) {
		// TODO Auto-generated method stub
		return cache.getCapControlPAO(paoID);
	}

	public List<CBCArea> getCbcAreas() {
		List<CBCArea> aList = cache.getCbcAreas();
		List<CBCArea> retList = new ArrayList<CBCArea>(aList.size());
		
		for( CBCArea a : aList ){
			if ( filter.valid(a) )
				retList.add(a);
		}
		return retList;
	}

	public Feeder getFeeder(Integer feederID) {
		int id = cache.getParentAreaID(feederID);
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cache.getFeeder(feederID);
		else
			return null;
	}

	public List<Feeder> getFeedersByArea(Integer areaID) {
		CBCArea area = cache.getCBCArea(areaID);
		if ( filter.valid(area) )
			return cache.getFeedersByArea(areaID);
		else
			return new ArrayList<Feeder>();
	}

	public List<Feeder> getFeedersBySubBus(Integer subBusID) {
		int id = cache.getParentAreaID(subBusID);
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cache.getFeedersBySubBus(subBusID);
		else
			return new ArrayList<Feeder>();
	}

	public List<Feeder> getFeedersBySubStation(SubStation sub) {
		int id = cache.getParentAreaID(sub.getCcId());
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cache.getFeedersBySubStation(sub);
		else
			return new ArrayList<Feeder>();
	}

	public LiteWrapper[] getOrphanedCBCs() {
		return cache.getOrphanedCBCs();
	}

	public LiteWrapper[] getOrphanedCapBanks() {
		return cache.getOrphanedCapBanks();
	}

	public LiteWrapper[] getOrphanedFeeders() {
		return cache.getOrphanedFeeders();
	}

	public LiteWrapper[] getOrphanedSubBuses() {
		return cache.getOrphanedSubBuses();
	}

	public LiteWrapper[] getOrphanedSubstations() {
		return cache.getOrphanedSubstations();
	}

	public int getParentSubBusID(int childID) {
		CBCArea area = cache.getCBCArea(childID);
		if ( filter.valid(area) )
			return cache.getParentSubBusID(childID);
		else
			return CtiUtilities.NONE_ZERO_ID;
	}

	public HashMap getSpecialAreaStateMap() {
		// TODO Auto-generated method stub
		return cache.getSpecialAreaStateMap();
	}

	public List<CBCSpecialArea> getSpecialCbcAreas() {
		List<CBCSpecialArea> aList = cache.getSpecialCbcAreas();
		List<CBCSpecialArea> retList = new ArrayList<CBCSpecialArea>(aList.size());
		
		for( CBCSpecialArea a : aList ){
			if ( filter.valid(a) )
				retList.add(a);
		}
		return retList;
	}

	public SubBus getSubBus(Integer subID) {
		int id = cache.getParentAreaID(subID);
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cache.getSubBus(subID);
		else
			return null;
	}

	public List<SubBus> getSubBusesByArea(Integer areaId) {
		CBCArea area = cache.getCBCArea(areaId);
		if ( filter.valid(area) )
			return cache.getSubBusesByArea(areaId);
		else
			return new ArrayList<SubBus>();
	}

	public List<SubBus> getSubBusesBySubStation(SubStation sub) {
		int id = cache.getParentAreaID(sub.getCcId());
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cache.getSubBusesBySubStation(sub);
		else
			return new ArrayList<SubBus>();	
	}

	public SubStation getSubstation(Integer subId) {
		int id = cache.getParentAreaID(subId);
		CBCArea area = cache.getCBCArea(id);
		if ( filter.valid(area) )
			return cache.getSubstation(subId);
		else
			return null;
		
	}

	public List<SubStation> getSubstationsByArea(Integer areaId) {
		CBCArea area = cache.getCBCArea(areaId);
		if ( filter.valid(area) )
			return cache.getSubstationsByArea(areaId);
		
        return Collections.emptyList();
	}

	public boolean isCBCArea(int id) {
		return cache.isCBCArea(id);
	}

	public boolean isCapBank(int id) {
		return cache.isCapBank(id);
	}

	public boolean isFeeder(int id) {
		return cache.isFeeder(id);
	}

	public boolean isSpecialCBCArea(int id) {
		return cache.isSpecialCBCArea(id);
	}

	public boolean isSubBus(int id) {
		return cache.isSubBus(id);
	}

    public CBCArea getArea(Integer areaId) {
        return cache.getArea(areaId);
    }

    public int getParentAreaID(int childID) {
        return cache.getParentAreaID(childID);
    }

    public CBCClientConnection getConnection() {
        return cache.getConnection();
    }

    public Boolean getSystemStatusOn() {
        return cache.getSystemStatusOn();
    }

    public CBCWebUpdatedObjectMap getUpdatedObjMap() {
        return cache.getUpdatedObjMap();
    }

    public CBCSpecialArea getSpecialArea(Integer areaId) {
        return cache.getSpecialArea(areaId);
    }
    
    public List<CapBankDevice> getCapBanksBySpecialArea(Integer areaID) {
        CBCSpecialArea area = cache.getCBCSpecialArea(areaID);
        if ( filter.valid(area) )
            return cache.getCapBanksByArea(areaID);

        return null;
    }
    
    public List<SubStation> getSubstationsBySpecialArea(Integer areaId) {
        CBCSpecialArea area = cache.getCBCSpecialArea(areaId);
        if ( filter.valid(area) )
            return cache.getSubstationsByArea(areaId);
        
        return Collections.emptyList();
    }

}

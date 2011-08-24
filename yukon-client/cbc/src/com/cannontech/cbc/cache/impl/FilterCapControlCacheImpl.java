package com.cannontech.cbc.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.filters.CacheFilter;
import com.cannontech.cbc.web.CBCWebUpdatedObjectMap;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlClientConnection;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.VoltageRegulatorFlags;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

/** Filter is going by the area right now, the other's will be as normal.
 * 
 * @author tspar
 *
 */

public class FilterCapControlCacheImpl implements CapControlCache {
    private static final String emptyName = "";
	private CapControlCache cache;
	private CacheFilter<StreamableCapObject> filter;
	
	public FilterCapControlCacheImpl() {

    }
	
	public void setFilter (CacheFilter<StreamableCapObject> filter) {
		this.filter = filter;
	}
	
    public void setCache(CapControlCache cache) {
        this.cache = cache;
    }
	
	public List<CCArea> getCbcAreas() {
        List<CCArea> aList = cache.getCbcAreas();
        List<CCArea> retList = new ArrayList<CCArea>();

        synchronized (aList) {
            for( CCArea a : aList ){
                if ( filter.valid(a) ) {
                    retList.add(a);
                }
            }
        }
        return retList;
    }
	
	public List<CCSpecialArea> getSpecialCbcAreas() {
        List<CCSpecialArea> aList = cache.getSpecialCbcAreas();
        List<CCSpecialArea> retList = new ArrayList<CCSpecialArea>(aList.size());
        
        for( CCSpecialArea a : aList ){
            if ( filter.valid(a) )
                retList.add(a);
        }
        return retList;
    }

	@Override
	public StreamableCapObject getArea(int paoId) throws NotFoundException {
	    StreamableCapObject object = cache.getArea(paoId);
	    if (filter.valid(object)) {
	        return object;
	    }
	    return null;
	}

	public CCArea getCBCArea(int id) {
		CCArea area = cache.getCBCArea(id);
		if (filter.valid(area)) return area;
		return null;
	}

    public VoltageRegulatorFlags getVoltageRegulatorFlags(int id) {
        VoltageRegulatorFlags regulatorFlags = cache.getVoltageRegulatorFlags(id);
        return regulatorFlags;
    }
    
	public CCSpecialArea getCBCSpecialArea(int id) {
		CCSpecialArea area = cache.getCBCSpecialArea(id);
		if (filter.valid(area)) return area;
			return null;
	}
	
	public StreamableCapObject getObject(int id) {
	    StreamableCapObject area = cache.getObject(id);
	    if (filter.valid(area)) return area;
	    return null;
	}

	public CapBankDevice getCapBankDevice(int capBankDeviceID) {
		CapBankDevice cap = cache.getCapBankDevice(capBankDeviceID);
		int id = cache.getParentAreaID(cap.getCcId());
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cap;
		return null;
	}

	public LiteState getCapBankState(int rawState) {
		return cache.getCapBankState(rawState);
	}

	public List<CapBankDevice> getCapBanksByArea(int areaID) {
		StreamableCapObject area = cache.getArea(areaID);
		if (filter.valid(area)) return cache.getCapBanksByArea(areaID);
		return null;
	}

	public List<CapBankDevice> getCapBanksByFeeder(int feederID) {
		int id = cache.getParentAreaID(feederID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getCapBanksByFeeder(feederID);
		return null;
	}

    public String getSubBusNameForFeeder(Feeder fdr){
        int id = cache.getParentAreaID(fdr.getCcId());
        StreamableCapObject area = cache.getArea(id);
        if (filter.valid(area)) return cache.getSubBusNameForFeeder(fdr);
        return emptyName;
    }
	public List<CapBankDevice> getCapBanksBySubBus(int subBusID) {
		int id = cache.getParentAreaID(subBusID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getCapBanksBySubBus(subBusID);
		return null;
	}

	public List<CapBankDevice> getCapBanksBySubStation(SubStation sub) {
	    if (sub == null) return Collections.emptyList();
	    
	    int id = cache.getParentAreaID(sub.getCcId());
	    if (id == 0) return Collections.emptyList();
	    StreamableCapObject area = cache.getArea(id);
	    if (filter.valid(area)) return cache.getCapBanksBySubStation(sub);
	    return Collections.emptyList();
	}

	public StreamableCapObject getCapControlPAO(int paoID) {
		return cache.getCapControlPAO(paoID);
	}

	public Feeder getFeeder(int feederID) {
		int id = cache.getParentAreaID(feederID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getFeeder(feederID);
		return null;
	}

	public List<Feeder> getFeedersByArea(int areaID) {
		StreamableCapObject area = cache.getArea(areaID);
		if (filter.valid(area)) return cache.getFeedersByArea(areaID);
		return Collections.emptyList();
	}

	public List<Feeder> getFeedersBySubBus(int subBusID) {
		int id = cache.getParentAreaID(subBusID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getFeedersBySubBus(subBusID);
		return Collections.emptyList();
	}

	public List<Feeder> getFeedersBySubStation(SubStation sub) {
	    if (sub == null) return Collections.emptyList();

	    int id = cache.getParentAreaID(sub.getCcId());
	    if(id == 0){
	    	return Collections.emptyList();
	    }
	    StreamableCapObject area = cache.getArea(id);
	    if (filter.valid(area)) return cache.getFeedersBySubStation(sub);
	    return Collections.emptyList();
	}

	public int getParentSubBusID(int childID) {
		int id = cache.getParentAreaID(childID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getParentSubBusID(childID);
		return CtiUtilities.NONE_ZERO_ID;
	}

	public SubBus getSubBus(int subID) {
		int id = cache.getParentAreaID(subID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getSubBus(subID);
		return null;
	}

	public List<SubBus> getSubBusesByArea(int areaId) {
		StreamableCapObject area = cache.getArea(areaId);
		if (filter.valid(area)) return cache.getSubBusesByArea(areaId);
	    return Collections.emptyList();
	}

	public List<SubBus> getSubBusesBySubStation(SubStation sub) {
	    if (sub == null) return Collections.emptyList();

	    int id = cache.getParentAreaID(sub.getCcId());
	    StreamableCapObject area = cache.getArea(id);
	    if (filter.valid(area)) return cache.getSubBusesBySubStation(sub);
	    return Collections.emptyList();
	}

	public SubStation getSubstation(int subId) {
		int id = cache.getParentAreaID(subId);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getSubstation(subId);
		return null;
	}

	public List<SubStation> getSubstationsByArea(int areaId) {
		StreamableCapObject area = cache.getArea(areaId);
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

    public int getParentAreaID(int childID) {
        return cache.getParentAreaID(childID);
    }

    public CapControlClientConnection getConnection() {
        return cache.getConnection();
    }

    public Boolean getSystemStatusOn() {
        return cache.getSystemStatusOn();
    }

    public CBCWebUpdatedObjectMap getUpdatedObjMap() {
        return cache.getUpdatedObjMap();
    }

    public List<CapBankDevice> getCapBanksBySpecialArea(int areaId) {
        CCSpecialArea area = cache.getCBCSpecialArea(areaId);
        if (filter.valid(area)) {
            return cache.getCapBanksByArea(areaId);
        }
        
        return null;
    }
    
    public List<CapBankDevice> getCapBanksByTypeAndId(CapControlType type, int id) {
    	
    	int areaId = getParentAreaID(id);
    	StreamableCapObject area = cache.getArea(areaId);
	    if (filter.valid(area))
	    	return cache.getCapBanksByTypeAndId(type,id);
    	
    	return null;
    }
    
    public List<SubStation> getSubstationsBySpecialArea(int areaId) {
        CCSpecialArea area = cache.getCBCSpecialArea(areaId);
        if (filter.valid(area)) {
            return cache.getSubstationsByArea(areaId);
        }
        
        return Collections.emptyList();
    }

	@Override
	public CapBankDevice getCapBankDeviceByStatusPointID(int capBankStatusId) {
		CapBankDevice capBank = cache.getCapBankDeviceByStatusPointID(capBankStatusId);

		if (capBank == null) {
			return null;
		}
		
		int areaId = getParentAreaID(capBank.getCcId());
    	StreamableCapObject area = cache.getArea(areaId);
	    if (filter.valid(area)) {
	    	return capBank;
	    }
    	
    	return null;
		
	}
}
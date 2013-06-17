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
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.yukon.conns.CapControlClientConnection;

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
	
	@Override
    public List<Area> getCbcAreas() {
        List<Area> aList = cache.getCbcAreas();
        List<Area> retList = new ArrayList<Area>();

        synchronized (aList) {
            for( Area a : aList ){
                if ( filter.valid(a) ) {
                    retList.add(a);
                }
            }
        }
        return retList;
    }
	
	@Override
    public List<SpecialArea> getSpecialCbcAreas() {
        List<SpecialArea> aList = cache.getSpecialCbcAreas();
        List<SpecialArea> retList = new ArrayList<SpecialArea>(aList.size());
        
        for( SpecialArea a : aList ){
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

	@Override
    public Area getCBCArea(int id) {
		Area area = cache.getCBCArea(id);
		if (filter.valid(area)) return area;
		return null;
	}

    @Override
    public VoltageRegulatorFlags getVoltageRegulatorFlags(int id) {
        VoltageRegulatorFlags regulatorFlags = cache.getVoltageRegulatorFlags(id);
        return regulatorFlags;
    }
    
	@Override
    public SpecialArea getCBCSpecialArea(int id) {
		SpecialArea area = cache.getCBCSpecialArea(id);
		if (filter.valid(area)) return area;
			return null;
	}
	
	@Override
    public StreamableCapObject getObject(int id) {
	    StreamableCapObject area = cache.getObject(id);
	    if (filter.valid(area)) return area;
	    return null;
	}

	@Override
    public CapBankDevice getCapBankDevice(int capBankDeviceID) {
		CapBankDevice cap = cache.getCapBankDevice(capBankDeviceID);
		int id = cache.getParentAreaID(cap.getCcId());
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cap;
		return null;
	}

	@Override
    public LiteState getCapBankState(int rawState) {
		return cache.getCapBankState(rawState);
	}

	@Override
    public List<CapBankDevice> getCapBanksByArea(int areaID) {
		StreamableCapObject area = cache.getArea(areaID);
		if (filter.valid(area)) return cache.getCapBanksByArea(areaID);
		return null;
	}

	@Override
    public List<CapBankDevice> getCapBanksByFeeder(int feederID) {
		int id = cache.getParentAreaID(feederID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getCapBanksByFeeder(feederID);
		return null;
	}

    @Override
    public String getSubBusNameForFeeder(Feeder fdr){
        int id = cache.getParentAreaID(fdr.getCcId());
        StreamableCapObject area = cache.getArea(id);
        if (filter.valid(area)) return cache.getSubBusNameForFeeder(fdr);
        return emptyName;
    }
	@Override
    public List<CapBankDevice> getCapBanksBySubBus(int subBusID) {
		int id = cache.getParentAreaID(subBusID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getCapBanksBySubBus(subBusID);
		return null;
	}

	@Override
    public List<CapBankDevice> getCapBanksBySubStation(SubStation sub) {
	    if (sub == null) return Collections.emptyList();
	    
	    int id = cache.getParentAreaID(sub.getCcId());
	    if (id == 0) return Collections.emptyList();
	    StreamableCapObject area = cache.getArea(id);
	    if (filter.valid(area)) return cache.getCapBanksBySubStation(sub);
	    return Collections.emptyList();
	}

	@Override
    public StreamableCapObject getCapControlPAO(int paoID) {
		return cache.getCapControlPAO(paoID);
	}

	@Override
    public Feeder getFeeder(int feederID) {
		int id = cache.getParentAreaID(feederID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getFeeder(feederID);
		return null;
	}

	@Override
    public List<Feeder> getFeedersByArea(int areaID) {
		StreamableCapObject area = cache.getArea(areaID);
		if (filter.valid(area)) return cache.getFeedersByArea(areaID);
		return Collections.emptyList();
	}

	@Override
    public List<Feeder> getFeedersBySubBus(int subBusID) {
		int id = cache.getParentAreaID(subBusID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getFeedersBySubBus(subBusID);
		return Collections.emptyList();
	}

	@Override
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

	@Override
    public int getParentSubBusID(int childID) {
		int id = cache.getParentAreaID(childID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getParentSubBusID(childID);
		return CtiUtilities.NONE_ZERO_ID;
	}
	
	@Override
    public SubBus getParentSubBus(int childID) {
	    int id = cache.getParentAreaID(childID);
	    StreamableCapObject area = cache.getArea(id);
	    if (filter.valid(area)) return cache.getParentSubBus(childID);
	    return null;
	}
	
	@Override
    public int getParentSubStationId(int childId) {
	    int id = cache.getParentAreaID(childId);
	    StreamableCapObject area = cache.getArea(id);
	    if (filter.valid(area)) return cache.getParentSubStationId(childId);
	    return CtiUtilities.NONE_ZERO_ID;
	}

	@Override
    public SubBus getSubBus(int subID) {
		int id = cache.getParentAreaID(subID);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getSubBus(subID);
		return null;
	}

	@Override
    public List<SubBus> getSubBusesByArea(int areaId) {
		StreamableCapObject area = cache.getArea(areaId);
		if (filter.valid(area)) return cache.getSubBusesByArea(areaId);
	    return Collections.emptyList();
	}

	@Override
    public List<SubBus> getSubBusesBySubStation(SubStation sub) {
	    if (sub == null) return Collections.emptyList();

	    int id = cache.getParentAreaID(sub.getCcId());
	    StreamableCapObject area = cache.getArea(id);
	    if (filter.valid(area)) return cache.getSubBusesBySubStation(sub);
	    return Collections.emptyList();
	}

	@Override
    public SubStation getSubstation(int subId) {
		int id = cache.getParentAreaID(subId);
		StreamableCapObject area = cache.getArea(id);
		if (filter.valid(area)) return cache.getSubstation(subId);
		return null;
	}

	@Override
    public List<SubStation> getSubstationsByArea(int areaId) {
		StreamableCapObject area = cache.getArea(areaId);
		if ( filter.valid(area) )
			return cache.getSubstationsByArea(areaId);
		
        return Collections.emptyList();
	}

	@Override
    public boolean isCBCArea(int id) {
		return cache.isCBCArea(id);
	}

	@Override
    public boolean isCapBank(int id) {
		return cache.isCapBank(id);
	}

	@Override
    public boolean isFeeder(int id) {
		return cache.isFeeder(id);
	}

	@Override
    public boolean isSpecialCBCArea(int id) {
		return cache.isSpecialCBCArea(id);
	}

	@Override
    public boolean isSubBus(int id) {
		return cache.isSubBus(id);
	}

    @Override
    public int getParentAreaID(int childID) {
        return cache.getParentAreaID(childID);
    }

    @Override
    public CapControlClientConnection getConnection() {
        return cache.getConnection();
    }

    @Override
    public Boolean getSystemStatusOn() {
        return cache.getSystemStatusOn();
    }

    @Override
    public CBCWebUpdatedObjectMap getUpdatedObjMap() {
        return cache.getUpdatedObjMap();
    }

    @Override
    public List<CapBankDevice> getCapBanksBySpecialArea(int areaId) {
        SpecialArea area = cache.getCBCSpecialArea(areaId);
        if (filter.valid(area)) {
            return cache.getCapBanksByArea(areaId);
        }
        
        return null;
    }
    
    @Override
    public List<CapBankDevice> getCapBanksByTypeAndId(CapControlType type, int id) {
    	
    	int areaId = getParentAreaID(id);
    	StreamableCapObject area = cache.getArea(areaId);
	    if (filter.valid(area))
	    	return cache.getCapBanksByTypeAndId(type,id);
    	
    	return null;
    }
    
    @Override
    public List<SubStation> getSubstationsBySpecialArea(int areaId) {
        SpecialArea area = cache.getCBCSpecialArea(areaId);
        if (filter.valid(area)) {
            return cache.getSubstationsByArea(areaId);
        }
        
        return Collections.emptyList();
    }

    
}
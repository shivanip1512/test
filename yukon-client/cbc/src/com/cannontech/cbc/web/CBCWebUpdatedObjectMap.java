package com.cannontech.cbc.web;

import java.util.Date;
import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.WebUpdatedPAObjectMap;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

public class CBCWebUpdatedObjectMap extends WebUpdatedPAObjectMap<Integer>{
    private CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
    
	public CBCWebUpdatedObjectMap() {
	}
	
	public void handleCBCChangeEvent(CCArea area, Date date) {
	    List<SubStation> subList = capControlCache.getSubstationsByArea(area.getPaoID());
	    for (final SubStation substation : subList) {
	        handleCBCChangeEvent(substation, date);
	    }
	    updateMap(area.getPaoID(), date);
	}
	
	public void handleCBCChangeEvent(CCSpecialArea specialArea, Date date) {
	    List<SubStation> substationList = capControlCache.getSubstationsBySpecialArea(specialArea.getPaoID());
	    for (final SubStation subStation : substationList) {
	        handleCBCChangeEvent(subStation, date);
	    }
	    updateMap(specialArea.getPaoID(), date);
	}
	
	public void handleCBCChangeEvent(SubStation subStation, Date date) {
	    List<SubBus> subBusList = capControlCache.getSubBusesBySubStation(subStation);
	    for (final SubBus sub : subBusList) {
            handleCBCChangeEvent(sub, date);
        }
	    updateMap(subStation.getCcId(), date);
	}
	
	public void handleCBCChangeEvent(SubBus subBus, Date date) {
	    List<Feeder> feederList = capControlCache.getFeedersBySubBus(subBus.getCcId());
	    for (final Feeder feeder : feederList) {
	        handleCBCChangeEvent(feeder, date);
	    }
	    updateMap(subBus.getCcId(), date);
	}
	
	public void handleCBCChangeEvent(Feeder feeder, Date date) {
	    CapBankDevice[] capList = capControlCache.getCapBanksByFeeder(feeder.getCcId());
	    for (final CapBankDevice cap : capList) {
	        handleCBCChangeEvent(cap, date);
	    }
	    updateMap(feeder.getCcId(), date);
	}
	
    public void handleCBCChangeEvent(CapBankDevice capBankDevice, Date date) {
        updateMap(capBankDevice.getCcId(), date);
    }

}


package com.cannontech.cbc.web;

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
	
	public void handleCBCChangeEvent(CCArea area) {
	    List<SubStation> subList = capControlCache.getSubstationsByArea(area.getPaoID());
	    for (final SubStation substation : subList) {
	        handleCBCChangeEvent(substation);
	    }
	    updateMap(area.getPaoID());
	}
	
	public void handleCBCChangeEvent(CCSpecialArea specialArea) {
	    List<SubStation> substationList = capControlCache.getSubstationsBySpecialArea(specialArea.getPaoID());
	    for (final SubStation subStation : substationList) {
	        handleCBCChangeEvent(subStation);
	    }
	    updateMap(specialArea.getPaoID());
	}
	
	public void handleCBCChangeEvent(SubStation subStation) {
	    List<SubBus> subBusList = capControlCache.getSubBusesBySubStation(subStation);
	    for (final SubBus sub : subBusList) {
            handleCBCChangeEvent(sub);
        }
	    updateMap(subStation.getCcId());
	}
	
	public void handleCBCChangeEvent(SubBus subBus) {
	    List<Feeder> feederList = capControlCache.getFeedersBySubBus(subBus.getCcId());
	    for (final Feeder feeder : feederList) {
	        handleCBCChangeEvent(feeder);
	    }
	    updateMap(subBus.getCcId());
	}
	
	public void handleCBCChangeEvent(Feeder feeder) {
	    CapBankDevice[] capList = capControlCache.getCapBanksByFeeder(feeder.getCcId());
	    for (final CapBankDevice cap : capList) {
	        handleCBCChangeEvent(cap);
	    }
	    updateMap(feeder.getCcId());
	}
	
    public void handleCBCChangeEvent(CapBankDevice capBankDevice) {
        updateMap(capBankDevice.getCcId());
    }

}


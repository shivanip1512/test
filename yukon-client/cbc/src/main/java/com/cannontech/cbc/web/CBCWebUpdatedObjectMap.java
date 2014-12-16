package com.cannontech.cbc.web;

import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.WebUpdatedPAObjectMap;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.spring.YukonSpringHook;

public class CBCWebUpdatedObjectMap extends WebUpdatedPAObjectMap<Integer> {
    
    private CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
    
    public CBCWebUpdatedObjectMap() {
    }
    
    public void handleCBCChangeEvent(Area area) {
        List<SubStation> subList = capControlCache.getSubstationsByArea(area.getCcId());
        for (final SubStation substation : subList) {
            handleCBCChangeEvent(substation);
        }
        updateMap(area.getCcId());
    }
    
    public void handleCBCChangeEvent(SpecialArea specialArea) {
        List<SubStation> substationList = capControlCache.getSubstationsBySpecialArea(specialArea.getCcId());
        for (final SubStation subStation : substationList) {
            handleCBCChangeEvent(subStation);
        }
        updateMap(specialArea.getCcId());
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
        List<CapBankDevice> capList = capControlCache.getCapBanksByFeeder(feeder.getCcId());
        for (final CapBankDevice cap : capList) {
            handleCBCChangeEvent(cap);
        }
        updateMap(feeder.getCcId());
    }
    
    public void handleCBCChangeEvent(CapBankDevice capBankDevice) {
        updateMap(capBankDevice.getCcId());
    }
    
    public void handleCBCChangeEvent(VoltageRegulatorFlags regulator) {
        updateMap(regulator.getCcId());
    }

}

package com.cannontech.web.capcontrol.util;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.models.ViewableSubStation;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

public class CapControlWebUtils {
    
    public static List<ViewableSubBus> createViewableSubBus(List<SubBus> subBusList, CapControlCache cache) {
        List<ViewableSubBus> viewableList = new ArrayList<ViewableSubBus>(subBusList.size());
        PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
        for(SubBus subBus: subBusList) {
            ViewableSubBus viewable = new ViewableSubBus();
            viewable.setSubBus(subBus);
            List<ViewableFeeder> viewableFeeders = createViewableFeeder(cache.getFeedersBySubBus(subBus.getCcId()), cache);
            viewable.setFeeders(viewableFeeders);
            if(subBus.getCurrentVarLoadPointID() != 0) {
                LitePoint point = pointDao.getLitePoint(subBus.getCurrentVarLoadPointID());
                viewable.setVarPoint(point);
            }
            if(subBus.getCurrentVoltLoadPointID() != 0) {
                LitePoint point = pointDao.getLitePoint(subBus.getCurrentVoltLoadPointID());
                viewable.setVoltPoint(point);
            }
            if(subBus.getCurrentWattLoadPointID() != 0) {
                LitePoint point = pointDao.getLitePoint(subBus.getCurrentWattLoadPointID());
                viewable.setWattPoint(point);
            }

            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    public static List<ViewableFeeder> createViewableFeeder(List<Feeder> feeders, CapControlCache cache) {
        List<ViewableFeeder> viewableList = new ArrayList<ViewableFeeder>(feeders.size());
        
        for (Feeder feeder: feeders) {
            String subBusName = cache.getSubBusNameForFeeder(feeder);
            ViewableFeeder viewable = new ViewableFeeder();
            List<ViewableCapBank> viewableCapbanks = createViewableCapBank(cache.getCapBanksByFeeder(feeder.getCcId()));
            viewable.setCapBanks(viewableCapbanks);
            viewable.setFeeder(feeder);
            viewable.setSubBusName(subBusName);
            
            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    public static List<ViewableCapBank> createViewableCapBank(List<CapBankDevice> capBanks) {
        List<ViewableCapBank> viewableList = new ArrayList<ViewableCapBank>(capBanks.size());
        PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
        
        for (CapBankDevice cbc: capBanks) {
            LiteYukonPAObject controller = paoDao.getLiteYukonPAO(cbc.getControlDeviceID());
            ViewableCapBank viewable = new ViewableCapBank();
            
            viewable.setCapBankDevice(cbc);
            viewable.setControlDevice(controller);
            viewable.setTwoWayCbc(CBCUtils.isTwoWay(controller));
            viewable.setDevice701x(CBCUtils.is701xDevice(controller));
            
            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    public static List<ViewableArea> createViewableAreas(List<? extends StreamableCapObject> areas, CapControlCache cache, boolean isSpecialArea) {
        List<ViewableArea> viewableList = new ArrayList<ViewableArea>(areas.size());
        
        for (StreamableCapObject area: areas) {         
            ViewableArea viewable = new ViewableArea();
            List<ViewableSubStation> viewableSubStations = null;
            
            List<SubStation> subStations = null;
            if(isSpecialArea) {
                subStations = cache.getSubstationsBySpecialArea(area.getCcId());
            } else {
                subStations = cache.getSubstationsByArea(area.getCcId());
            }
            viewableSubStations = createViewableSubStation(subStations, cache);

            viewable.setArea(area);
            viewable.setSubStations(viewableSubStations);
            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    public static List<ViewableSubStation> createViewableSubStation(List<SubStation> subStations, CapControlCache cache) {
        List<ViewableSubStation> viewableList = new ArrayList<ViewableSubStation>(subStations.size());
        
        for(SubStation subStation : subStations) {
            ViewableSubStation viewable = new ViewableSubStation();
            List<ViewableSubBus> subBuses = createViewableSubBus(cache.getSubBusesBySubStation(subStation), cache) ;
            List<CapBankDevice> capBanks = cache.getCapBanksBySubStation(subStation);
            List<Feeder> feeders = cache.getFeedersBySubStation(subStation);
            
            viewable.setSubstation(subStation);
            viewable.setSubStationName(subStation.getCcName());
            viewable.setFeederCount(feeders.size());
            viewable.setCapBankCount(capBanks.size());
            viewable.setSubBuses(subBuses);
            
            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
}

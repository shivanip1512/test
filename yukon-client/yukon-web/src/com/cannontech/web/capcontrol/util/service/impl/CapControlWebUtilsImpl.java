package com.cannontech.web.capcontrol.util.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.models.NavigableArea;
import com.cannontech.web.capcontrol.models.NavigableCapBank;
import com.cannontech.web.capcontrol.models.NavigableFeeder;
import com.cannontech.web.capcontrol.models.NavigableSubstation;
import com.cannontech.web.capcontrol.models.NavigableSubstationBus;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.models.ViewableSubStation;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtils;
import com.google.common.collect.ImmutableSet;

public class CapControlWebUtilsImpl implements CapControlWebUtils {

    private final ImmutableSet<ControlAlgorithm> showToolTipAlgorithms = ImmutableSet.of(ControlAlgorithm.PFACTOR_KW_KVAR);

    @Override
    public List<ViewableSubBus> createViewableSubBus(List<SubBus> subBusList) {
        List<ViewableSubBus> viewableList = new ArrayList<ViewableSubBus>(subBusList.size());
        PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
        CapControlCache cache = YukonSpringHook.getBean("capControlCache", CapControlCache.class);
        
        for(SubBus subBus: subBusList) {
            ViewableSubBus viewable = new ViewableSubBus();
            viewable.setSubBus(subBus);
            viewable.setIvvcControlled(subBus.getControlUnits() == ControlAlgorithm.INTEGRATED_VOLT_VAR);
            viewable.setShowTargetTooltip(showToolTipAlgorithms.contains(subBus.getControlUnits()));
            
            int alternateStationId = 0;
            int alternateAreaId = 0;
            
            if (subBus.getAlternateBusId() > 0) {
                SubBus linkedSub = cache.getSubBus(subBus.getAlternateBusId());
                SubStation station = cache.getSubstation(linkedSub.getParentID());
                
                alternateStationId = station.getCcId();
                alternateAreaId = station.getParentID();
            }

            viewable.setAlternateStationId(alternateStationId);
            viewable.setAlternateAreaId(alternateAreaId);

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

            if(subBus.getStrategyId() > 0) {
                //Check to see if we are an IVVC enabled
                StrategyDao strategyDao = YukonSpringHook.getBean("strategyDao", StrategyDao.class);
                CapControlStrategy strategy = strategyDao.getForId(subBus.getStrategyId());
                if(strategy.getControlUnits() == ControlAlgorithm.INTEGRATED_VOLT_VAR) {
                    viewable.setIvvcControlled(true);
                }
            }
            
            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    @Override
    public List<ViewableFeeder> createViewableFeeder(List<Feeder> feeders, CapControlCache cache) {
        List<ViewableFeeder> viewableList = new ArrayList<ViewableFeeder>(feeders.size());
        
        for (Feeder feeder: feeders) {
            String subBusName = cache.getSubBusNameForFeeder(feeder);
            ViewableFeeder viewable = new ViewableFeeder();
            
            viewable.setFeeder(feeder);
            viewable.setSubBusName(subBusName);
            ControlAlgorithm algorithm = feeder.getControlUnits();
            viewable.setIvvcControlled(algorithm == ControlAlgorithm.INTEGRATED_VOLT_VAR);
            viewable.setShowTargetTooltip(showToolTipAlgorithms.contains(algorithm));
            
            if (feeder.getOriginalParentId() > 0) {
                viewable.setMovedFeeder(true); 
            } else {
                viewable.setMovedFeeder(false);
            }
            
            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    @Override
    public List<ViewableCapBank> createViewableCapBank(List<CapBankDevice> capBanks) {
        List<ViewableCapBank> viewableList = new ArrayList<ViewableCapBank>(capBanks.size());
        PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
        for (CapBankDevice cbc: capBanks) {
            LiteYukonPAObject controller = paoDao.getLiteYukonPAO(cbc.getControlDeviceID());
            ViewableCapBank viewable = new ViewableCapBank();
            
            viewable.setCapBankDevice(cbc);
            viewable.setControlDevice(controller);
            viewable.setTwoWayCbc(CapControlUtils.isTwoWay(controller));
            viewable.setDevice701x(CapControlUtils.is701xDevice(controller));
            
            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    @Override
    public List<ViewableArea> createViewableAreas(List<? extends StreamableCapObject> areas, CapControlCache cache, boolean isSpecialArea) {
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
    
    @Override
    public List<ViewableSubStation> createViewableSubStation(List<SubStation> subStations, CapControlCache cache) {
        List<ViewableSubStation> viewableList = new ArrayList<ViewableSubStation>(subStations.size());
        
        for(SubStation subStation : subStations) {
            ViewableSubStation viewable = new ViewableSubStation();
            List<CapBankDevice> capBanks = cache.getCapBanksBySubStation(subStation);
            List<Feeder> feeders = cache.getFeedersBySubStation(subStation);
            
            viewable.setName(subStation.getCcName());
            viewable.setFeederCount(feeders.size());
            viewable.setCapBankCount(capBanks.size());
            
            viewableList.add(viewable);
        }
        
        return viewableList;
    }

    @Override
    public List<NavigableArea> buildSimpleHierarchy(){
        List<NavigableArea> areas = new ArrayList<NavigableArea>();
        CapControlCache cache = YukonSpringHook.getBean("capControlCache", CapControlCache.class);
        for(Area area : cache.getCbcAreas()) {
            NavigableArea navigableArea = new NavigableArea(getSimpleSubstations(area));
            navigableArea.setName(area.getCcName());
            navigableArea.setId(area.getCcId());
            areas.add(navigableArea);
        }
        return areas;
    }
    
    @Override
    public List<NavigableSubstation> getSimpleSubstations(Area area){
        List<NavigableSubstation> substations = new ArrayList<NavigableSubstation>();
        CapControlCache cache = YukonSpringHook.getBean("capControlCache", CapControlCache.class);
        for(SubStation substation : cache.getSubstationsByArea(area.getCcId())) {
            NavigableSubstation navigableSubstation = new NavigableSubstation(getSimpleSubstationBuses(substation));
            navigableSubstation.setName(substation.getCcName());
            navigableSubstation.setId(substation.getCcId());
            substations.add(navigableSubstation);
        }
        return substations;
    }
    
    @Override
    public List<NavigableSubstationBus> getSimpleSubstationBuses(SubStation substation){
        List<NavigableSubstationBus> substationBuses = new ArrayList<NavigableSubstationBus>();
        CapControlCache cache = YukonSpringHook.getBean("capControlCache", CapControlCache.class);
        for(SubBus substationBus : cache.getSubBusesBySubStation(substation)) {
            NavigableSubstationBus navigableSubstationBus = new NavigableSubstationBus(getSimpleFeeders(substationBus));
            navigableSubstationBus.setName(substationBus.getCcName());
            navigableSubstationBus.setId(substationBus.getCcId());
            substationBuses.add(navigableSubstationBus);
        }
        return substationBuses;
    }
    
    @Override
    public List<NavigableFeeder> getSimpleFeeders(SubBus subBus){
        List<NavigableFeeder> feeders = new ArrayList<NavigableFeeder>();
        CapControlCache cache = YukonSpringHook.getBean("capControlCache", CapControlCache.class);
        for(Feeder feeder : cache.getFeedersBySubBus(subBus.getCcId())) {
            NavigableFeeder navigableFeeder = new NavigableFeeder(getSimpleCapBanks(feeder));
            navigableFeeder.setName(feeder.getCcName());
            navigableFeeder.setId(feeder.getCcId());
            feeders.add(navigableFeeder);
        }
        return feeders;
    }
    
    @Override
    public List<NavigableCapBank> getSimpleCapBanks(Feeder feeder){
        List<NavigableCapBank> capbanks = new ArrayList<NavigableCapBank>();
        CapControlCache cache = YukonSpringHook.getBean("capControlCache", CapControlCache.class);
        for(CapBankDevice capBank : cache.getCapBanksByFeeder(feeder.getCcId())) {
            NavigableCapBank navigableCapBank = new NavigableCapBank();
            navigableCapBank.setName(capBank.getCcName());
            navigableCapBank.setId(capBank.getCcId());
            capbanks.add(navigableCapBank);
        }
        return capbanks;
    }
    
    @Override
    public String getCapControlFacesEditorLinkHtml(int ccId, YukonUserContext userContext) {
        String name = getPaoNameWithId(ccId, userContext);
        String html = getLinkHtml("/editor/cbcBase.jsf?type=" + DBEditorTypes.EDITOR_CAPCONTROL
                                + "&amp;itemid=" + ccId, name, new HashMap<String, String>());
        return html;
    }
    
    private String getPaoNameWithId(int paoId, YukonUserContext userContext) throws NotFoundException {
        PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
        PaoLoadingService paoLoadingService = YukonSpringHook.getBean(PaoLoadingService.class);
        YukonPao yukonPao = paoDao.getYukonPao(paoId);
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(yukonPao);
        return displayablePao.getName();
    }
    
    private String getLinkHtml(String url, String value, Map<String, String> argMap) {
        String html = "<a href=\"" + url;
        for (Entry<String, String> argEntry : argMap.entrySet()) {
            html += "?" + argEntry.getKey() + "=" + argEntry.getValue();
        }
        html += "\">" + value + "</a>";
        return html;
    }
}
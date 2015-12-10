package com.cannontech.web.capcontrol.util.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.web.capcontrol.models.NavigableArea;
import com.cannontech.web.capcontrol.models.NavigableCapBank;
import com.cannontech.web.capcontrol.models.NavigableFeeder;
import com.cannontech.web.capcontrol.models.NavigableSubstation;
import com.cannontech.web.capcontrol.models.NavigableSubstationBus;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;

public class CapControlWebUtilsServiceImpl implements CapControlWebUtilsService {

    private static final ImmutableSet<ControlAlgorithm> showToolTipAlgorithms = ImmutableSet.of(ControlAlgorithm.PFACTOR_KW_KVAR);

    @Autowired private CapControlCache capControlCache;
    @Autowired private IDatabaseCache dbCache;

    @Override
    public List<ViewableSubBus> createViewableSubBus(List<SubBus> subBusList) {

        List<ViewableSubBus> viewableList = new ArrayList<>();
        
        for (SubBus subBus: subBusList) {
            ViewableSubBus viewable = new ViewableSubBus();
            viewable.setSubBusInfo(subBus);
            viewable.setShowTargetTooltip(showToolTipAlgorithms.contains(subBus.getAlgorithm()));
            
            if (subBus.getAlternateBusId() > 0) {
                SubBus linkedSub = capControlCache.getSubBus(subBus.getAlternateBusId());
                
                try {
                    SubStation linkedStation = capControlCache.getSubstation(linkedSub.getParentID());
                    viewable.setAlternateStationId(linkedStation.getCcId());
                    viewable.setAlternateAreaId(linkedStation.getParentID());
                } catch (NotFoundException exception) {
                    //Alternate dual bus is currently an orphan.
                }
            }

            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    @Override
    public List<ViewableFeeder> createViewableFeeder(List<Feeder> feeders) {

        List<ViewableFeeder> viewableList = new ArrayList<ViewableFeeder>(feeders.size());
        
        for (Feeder feeder: feeders) {
            ViewableFeeder viewable = new ViewableFeeder();
            viewable.setFeederInfo(feeder);
            ControlAlgorithm algorithm = feeder.getAlgorithm();
            viewable.setShowTargetTooltip(showToolTipAlgorithms.contains(algorithm));

            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    @Override
    public List<ViewableCapBank> createViewableCapBank(List<CapBankDevice> capBanks) {

        List<ViewableCapBank> viewableList = new ArrayList<ViewableCapBank>(capBanks.size());
        Map<Integer, LiteYukonPAObject> allPaos = dbCache.getAllPaosMap();

        for (CapBankDevice bank: capBanks) {
            LiteYukonPAObject cbc = allPaos.get(bank.getControlDeviceID());
            ViewableCapBank viewable = new ViewableCapBank();

            viewable.setBankInfo(bank);
            viewable.setCbcInfo(cbc);

            viewableList.add(viewable);
        }
        
        return viewableList;
    }
    
    @Override
    public List<ViewableArea> createViewableAreas(List<? extends StreamableCapObject> areas, CapControlCache cache, boolean isSpecialArea) {
        List<ViewableArea> viewableList = new ArrayList<ViewableArea>(areas.size());
        
        for (StreamableCapObject area: areas) {
            ViewableArea viewableArea = new ViewableArea();

            List<SubStation> subStations = null;
            if(isSpecialArea) {
                subStations = cache.getSubstationsBySpecialArea(area.getCcId());
            } else {
                subStations = cache.getSubstationsByArea(area.getCcId());
            }

            viewableArea.setAreaInfo(area);
            viewableArea.setStationCount(subStations.size());
            viewableList.add(viewableArea);
        }
        
        return viewableList;
    }

    @Override
    public List<NavigableArea> buildSimpleHierarchy(){
        List<NavigableArea> areas = new ArrayList<NavigableArea>();
        for(Area area : capControlCache.getAreas()) {
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
        for(SubStation substation : capControlCache.getSubstationsByArea(area.getCcId())) {
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
        for(SubBus substationBus : capControlCache.getSubBusesBySubStation(substation)) {
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
        for(Feeder feeder : capControlCache.getFeedersBySubBus(subBus.getCcId())) {
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
        for(CapBankDevice capBank : capControlCache.getCapBanksByFeeder(feeder.getCcId())) {
            NavigableCapBank navigableCapBank = new NavigableCapBank();
            navigableCapBank.setName(capBank.getCcName());
            navigableCapBank.setId(capBank.getCcId());
            capbanks.add(navigableCapBank);
        }
        return capbanks;
    }
    
    @Override
    public String getCapControlFacesEditorLinkHtml(HttpServletRequest request, int ccId) {

        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(ccId);
        String name = pao.getPaoName();

        String url;
        if (pao.getPaoType().isRegulator()) {
            url = request.getContextPath() + "/capcontrol/regulators/" + ccId;
        } else if (pao.getPaoType() == PaoType.CAP_CONTROL_AREA || pao.getPaoType() == PaoType.CAP_CONTROL_SPECIAL_AREA) {
            url = request.getContextPath() + "/capcontrol/areas/" + ccId;
        } else if (pao.getPaoType().isCbc()) {
            url = request.getContextPath() + "/capcontrol/cbc/" + ccId;
        } else {
            url= request.getContextPath() + "/editor/cbcBase.jsf?type=" + DBEditorTypes.EDITOR_CAPCONTROL
                    + "&amp;itemid=" + ccId;
        }
        String html = getLinkHtml(url, name);
        return html;
    }
    
    private String getLinkHtml(String url, String value) {
        String html = "<a href=\"" + url + "\">" + StringUtils.escapeXmlAndJavascript(value) + "</a>";
        return html;
    }
}
package com.cannontech.web.capcontrol.ivvc;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.web.CapControlCommandExecutor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.capcontrol.VoltageRegulatorPointMapping;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.CommandHolder;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.util.CapControlWebUtils;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubStation;
import com.cannontech.yukon.cbc.VoltageRegulatorFlags;
import com.google.common.collect.Lists;


@RequestMapping("/ivvc/zone/*")
@Controller
public class ZoneDetailController {

    private FilterCacheFactory filterCacheFactory;
    private RolePropertyDao rolePropertyDao;
    private ZoneService zoneService;
    private VoltageRegulatorService voltageRegulatorService;
    private VoltageFlatnessGraphService voltageFlatnessGraphService;
    
    @RequestMapping
    public String detail(ModelMap model, HttpServletRequest request, LiteYukonUser user, int zoneId, Boolean isSpecialArea) {
        if(isSpecialArea == null) {
            isSpecialArea = false;
        }
        model.addAttribute("isSpecialArea",isSpecialArea);        
        model.addAttribute("title", "IVVC Zone Detail View");
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole",hasEditingRole);
        
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        Zone zone = zoneService.getZoneById(zoneId);
        VoltageRegulatorFlags regulatorFlags = cache.getVoltageRegulatorFlags(zone.getRegulatorId());
        
        setupDetails(model,regulatorFlags);
        setupIvvcEvents(model,zone.getId(),zone.getSubstationBusId());
        setupCapBanks(model,cache,zone);
        setupBreadCrumbs(model, cache, zone, isSpecialArea);
        setupDeltas(model,request,cache,zone);
        setupRegulatorPointList(model,regulatorFlags.getCcId());
        setupRegulatorCommands(model);
        
        model.addAttribute("subBusId", zone.getSubstationBusId());
        
        return "ivvc/zoneDetail.jsp";
    }
    
    @RequestMapping
    public String deltaUpdate(ModelMap model, boolean staticDelta, int bankId, int pointId, double delta, 
                              int zoneId, Boolean isSpecialArea, LiteYukonUser user, HttpServletRequest request) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        CapControlCommandExecutor exec = new CapControlCommandExecutor(cache, user);
        
        exec.executeDeltaUpdate(bankId,pointId,delta,staticDelta);
        
        if(isSpecialArea == null) {
            isSpecialArea = false;
        }
        
        model.addAttribute("isSpecialArea", isSpecialArea);
        model.addAttribute("zoneId", zoneId);
        return "redirect:/spring/capcontrol/ivvc/zone/detail";
    }
    
    @RequestMapping
    public String chart(ModelMap model, YukonUserContext userContext, int zoneId) {
        VfGraph graph = voltageFlatnessGraphService.getZoneGraph(userContext, zoneId);
        
        model.addAttribute("graph", graph);
        model.addAttribute("graphSettings", graph.getSettings());
        
        return "ivvc/flatnessGraph.jsp";
    }
    
    private void setupDetails(ModelMap model, VoltageRegulatorFlags regulatorFlags) {
        model.addAttribute("regulatorId",regulatorFlags.getCcId());
        model.addAttribute("regulatorName",regulatorFlags.getCcName());
    }
    
    private void setupRegulatorCommands(ModelMap model) {
        //This will need to change when we differentiate between phase operated and gang operated Regulators
        model.addAttribute("regulatorType",CapControlType.LTC);
        
        model.addAttribute("scanCommandHolder",CommandHolder.LTC_SCAN_INTEGRITY);
        model.addAttribute("tapDownCommandHolder",CommandHolder.LTC_TAP_POSITION_LOWER);
        model.addAttribute("tapUpCommandHolder",CommandHolder.LTC_TAP_POSITION_RAISE);
        model.addAttribute("enableRemoteCommandHolder",CommandHolder.LTC_REMOTE_ENABLE);
        model.addAttribute("disableRemoteCommandHolder",CommandHolder.LTC_REMOTE_DISABLE);
    }
    
    private void setupIvvcEvents(ModelMap model,int zoneId,int subBusId) {
        final int rowLimit = 20;
        
        List<CcEvent> events = zoneService.getLatestEvents(zoneId, subBusId, rowLimit);
        
        model.addAttribute("events", events);
    }
    
    private void setupCapBanks(ModelMap model, CapControlCache cache, Zone zone) {
        List<Integer> capBankIdList = zoneService.getCapBankIdsForZoneId(zone.getId());
        List<Integer> unassignedBankIds = zoneService.getUnassignedCapBankIdsForSubBusId(zone.getSubstationBusId());
        
        //Add unassigned banks to main list, we want to display them.
        capBankIdList.addAll(unassignedBankIds);
        
        List<CapBankDevice> capBankList = Lists.newArrayList();
        for (Integer bankId : capBankIdList) {
            CapBankDevice bank = cache.getCapBankDevice(bankId);
            capBankList.add(bank);
        }
        
        List<ViewableCapBank> viewableCapBankList = CapControlWebUtils.createViewableCapBank(capBankList);
        for (ViewableCapBank bank:viewableCapBankList) {
            List<Integer> monitorPoints = zoneService.getMonitorPointsForBank(bank.getCapBankDevice().getCcId());
            if (monitorPoints.size() > 0) {
                //Grabbing the first one to display, list is sorted on display order
                bank.setVoltagePointId(monitorPoints.get(0));
            }
            
            if (unassignedBankIds.contains(bank.getCapBankDevice().getCcId())) {
                bank.setNotAssignedToZone(true);
            } else {
                bank.setNotAssignedToZone(false);
            }
        }

        model.addAttribute("unassignedBanksExist",unassignedBankIds.size()>0);
        model.addAttribute("capBankList", viewableCapBankList);
    }
    
    private void setupRegulatorPointList(ModelMap model, int regulatorId) {
        List<VoltageRegulatorPointMapping> pointMappings = voltageRegulatorService.getPointMappings(regulatorId);
        Collections.sort(pointMappings);
        model.addAttribute("regulatorPointMappings", pointMappings);
    }
    
    private void setupDeltas(ModelMap model, HttpServletRequest request, CapControlCache cache, Zone zone) {
        List<Integer> bankIds = zoneService.getCapBankIdsForZoneId(zone.getId());
        
        List<CapBankPointDelta> pointDeltas = zoneService.getAllPointDeltasForBankIds(bankIds);
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 10);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        
        SearchResult<CapBankPointDelta> searchResults = new SearchResult<CapBankPointDelta>();
        List<CapBankPointDelta> trimmedPointDeltas = Lists.newArrayList();
        
        if (startIndex < pointDeltas.size()) {
            int endIndex;
            
            if (startIndex + itemsPerPage > pointDeltas.size()) {
                endIndex = pointDeltas.size();
            } else {
                endIndex = startIndex + itemsPerPage;
            }
            
        	trimmedPointDeltas = pointDeltas.subList(startIndex,endIndex);
        }
        
        searchResults.setResultList(trimmedPointDeltas);
        searchResults.setBounds(startIndex, itemsPerPage, pointDeltas.size());
        
        model.addAttribute("zoneId", zone.getId());
        model.addAttribute("searchResults", searchResults);
    }
    
    private void setupBreadCrumbs(ModelMap model, CapControlCache cache, Zone zone, boolean isSpecialArea) {
        
        int subBusId = zone.getSubstationBusId();
        StreamableCapObject subBus = cache.getSubBus(subBusId);
        SubStation station = cache.getSubstation(subBus.getParentID());
        
        StreamableCapObject area;
        if(isSpecialArea) {
            area = cache.getArea(station.getSpecialAreaId());
        }
        else {
            area = cache.getArea(station.getParentID());
        }
        
        String areaName = area.getCcName();
        String substationName = station.getCcName();
        String subBusName = subBus.getCcName();
        String zoneName = zone.getName();
        
        model.addAttribute("areaId", area.getCcId());
        model.addAttribute("areaName", areaName);
        model.addAttribute("subStationId", station.getCcId());
        model.addAttribute("subStationName", substationName);
        model.addAttribute("subBusId", subBusId);
        model.addAttribute("subBusName", subBusName);
        model.addAttribute("zoneName", zoneName);
    }
    
    @Autowired
    public void setFilteredCapControlcache (FilterCacheFactory factory) {
        this.filterCacheFactory = factory;
    }
    
    @Autowired
    public void setRolePropertyDao (RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setZoneService (ZoneService zoneService) {
        this.zoneService = zoneService;
    }
    
    @Autowired
    public void setVoltageRegulatorService(VoltageRegulatorService voltageRegulatorService) {
        this.voltageRegulatorService = voltageRegulatorService;
    }
    
    @Autowired
    public void setVoltageFlatnessGraphService(VoltageFlatnessGraphService voltageFlatnessGraphService) {
        this.voltageFlatnessGraphService = voltageFlatnessGraphService;
    }
}

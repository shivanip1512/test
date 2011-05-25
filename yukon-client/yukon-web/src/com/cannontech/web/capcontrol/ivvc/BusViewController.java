package com.cannontech.web.capcontrol.ivvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.ZoneHierarchy;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubStation;


@RequestMapping("/ivvc/bus/*")
@Controller
public class BusViewController {

    private FilterCacheFactory filterCacheFactory;
    private RolePropertyDao rolePropertyDao;
    private ZoneService zoneService;
    private StrategyDao strategyDao;
    private VoltageFlatnessGraphService voltageFlatnessGraphService;
    
    @RequestMapping
    public String detail(ModelMap model, LiteYukonUser user, Boolean isSpecialArea, int subBusId) {
        setupDetails(model, user, isSpecialArea, subBusId);
        return "ivvc/busView.jsp";
    }
    
    private void setupDetails(ModelMap model, LiteYukonUser user, Boolean isSpecialArea, int subBusId) {
        if(isSpecialArea == null) {
            isSpecialArea = false;
        }
        model.addAttribute("isSpecialArea",isSpecialArea);
        model.addAttribute("title", "IVVC SubBus View");
        model.addAttribute("subBusId", subBusId);
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole",hasEditingRole);
        
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        setupBreadCrumbs(model,cache,subBusId,isSpecialArea);
        setupZoneList(model,cache,subBusId);
        setupStrategyDetails(model,cache,subBusId);

        model.addAttribute("gangOperatedZone", ZoneType.GANG_OPERATED);
        model.addAttribute("threePhaseOperatedZone", ZoneType.THREE_PHASE);
        model.addAttribute("singlePhaseZone", ZoneType.SINGLE_PHASE);
    }
    
    @RequestMapping
    public String chart(ModelMap model, FlashScope flash, YukonUserContext userContext, int subBusId) {
        try {
            VfGraph graph = voltageFlatnessGraphService.getSubBusGraph(userContext, subBusId);
            
            model.addAttribute("graph", graph);
            model.addAttribute("graphSettings", graph.getSettings());
        } catch (IllegalUseOfAttribute e) {
//            flash.setError(new YukonMessageSourceResolvable(
//            "yukon.web.modules.capcontrol.ivvc.busView.voltageProfile.missingVoltageAttribute"));
        }
        
        return "ivvc/flatnessGraphLine.jsp";
    }
    
    private void setupStrategyDetails(ModelMap model, CapControlCache cache, int subBusId) {
        int strategyId = cache.getSubBus(subBusId).getStrategyId();
        CapControlStrategy strategy = strategyDao.getForId(strategyId);

        model.addAttribute("strategyName",strategy.getStrategyName());
        model.addAttribute("strategySettings",strategy.getTargetSettings());
    }
    
    private void setupBreadCrumbs(ModelMap model, CapControlCache cache, int subBusId, boolean isSpecialArea) {
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
        
        model.addAttribute("areaId", area.getCcId());
        model.addAttribute("areaName", areaName);
        model.addAttribute("subStationId", station.getCcId());
        model.addAttribute("subStationName", substationName);
        model.addAttribute("subBusName", subBusName);
    }
    
    private void setupZoneList(ModelMap model, CapControlCache cache, int subBusId) {
        ZoneHierarchy hierarchy = zoneService.getZoneHierarchyBySubBusId(subBusId);
        model.addAttribute("zones",hierarchy);
        
        //Check for any unassigned banks and flag it
        List<Integer> unassignedBankIds = zoneService.getUnassignedCapBankIdsForSubBusId(subBusId);
        
        model.addAttribute("unassignedBanksExist",unassignedBankIds.size()>0);
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
    public void setStrategyDao (StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }
    
    @Autowired
    public void setVoltageFlatnessGraphService(VoltageFlatnessGraphService voltageFlatnessGraphService) {
        this.voltageFlatnessGraphService = voltageFlatnessGraphService;
    }
}

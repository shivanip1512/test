package com.cannontech.web.capcontrol.ivvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.model.ZoneHierarchy;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.capcontrol.ivvc.service.ZoneService;
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
        setupZoneList(model,subBusId);
        setupStrategyDetails(model,cache,subBusId);
        
        return "ivvc/busView.jsp";
    }
    
    @RequestMapping
    public String chart(ModelMap model, YukonUserContext userContext, int subBusId) {
        
        VfGraph graph = voltageFlatnessGraphService.getSubBusGraph(userContext, subBusId);
        model.addAttribute("graph", graph);
        model.addAttribute("graphSettings", graph.getSettings());
        
        return "ivvc/flatnessGraph.jsp";
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
    
    private void setupZoneList(ModelMap model, int subBusId) {
        ZoneHierarchy hierarchy = zoneService.getZoneHierarchyBySubBusId(subBusId);
        model.addAttribute("zones",hierarchy);
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

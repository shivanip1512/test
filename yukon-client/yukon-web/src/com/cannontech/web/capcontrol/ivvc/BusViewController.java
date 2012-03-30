package com.cannontech.web.capcontrol.ivvc;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.capcontrol.model.ZoneHierarchy;
import com.cannontech.capcontrol.model.ZoneVoltagePointsHolder;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.enums.Phase;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RequestMapping("/ivvc/bus/*")
@Controller
public class BusViewController {

    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ZoneService zoneService;
    @Autowired private StrategyDao strategyDao;
    @Autowired private VoltageFlatnessGraphService voltageFlatnessGraphService;
    @Autowired private PointDao pointDao;
    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;
    
    @RequestMapping
    public String detail(ModelMap model, YukonUserContext userContext, Boolean isSpecialArea, int subBusId) {
        setupDetails(model, userContext, isSpecialArea, subBusId);
        return "ivvc/busView.jsp";
    }
    
    private void setupDetails(ModelMap model, YukonUserContext userContext, Boolean isSpecialArea, int subBusId) {
        if(isSpecialArea == null) {
            isSpecialArea = false;
        }
        model.addAttribute("isSpecialArea",isSpecialArea);
        model.addAttribute("subBusId", subBusId);
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
        model.addAttribute("hasEditingRole",hasEditingRole);
        
        List<LitePoint> allSubBusPoints = pointDao.getLitePointsByPaObjectId(subBusId);
        model.addAttribute("allSubBusPoints", allSubBusPoints);
        
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(userContext.getYukonUser());
        
        setupBreadCrumbs(model, cache, subBusId, isSpecialArea, userContext);
        setupZoneList(model,cache,subBusId);
        setupStrategyDetails(model,cache,subBusId);

        model.addAttribute("gangOperatedZone", ZoneType.GANG_OPERATED);
        model.addAttribute("threePhaseOperatedZone", ZoneType.THREE_PHASE);
        model.addAttribute("singlePhaseZone", ZoneType.SINGLE_PHASE);
    }
    
    @RequestMapping
    public String chart(ModelMap model, FlashScope flash, YukonUserContext userContext, int subBusId) {
        boolean zoneAttributesExist = voltageFlatnessGraphService.
                                                allZonesHaveRequiredAttributes(subBusId,
                                                                               userContext.getYukonUser());
        model.addAttribute("zoneAttributesExist", zoneAttributesExist);
        if (zoneAttributesExist) {
            VfGraph graph = voltageFlatnessGraphService.getSubBusGraph(userContext, subBusId);
            model.addAttribute("graph", graph);
            model.addAttribute("graphSettings", graph.getSettings());
        }
        
        return "ivvc/flatnessGraphLine.jsp";
    }
    
    private void setupStrategyDetails(ModelMap model, CapControlCache cache, int subBusId) {
        int strategyId = cache.getSubBus(subBusId).getStrategyId();
        CapControlStrategy strategy = strategyDao.getForId(strategyId);

        model.addAttribute("strategyId",strategy.getStrategyID());
        model.addAttribute("strategyName",strategy.getStrategyName());
        model.addAttribute("strategySettings",strategy.getTargetSettings());
    }
    
    private void setupBreadCrumbs(ModelMap model, CapControlCache cache, int subBusId, boolean isSpecialArea, YukonUserContext userContext) {
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
        
        model.addAttribute("bc_areaId", area.getCcId());
        model.addAttribute("bc_areaName", areaName);
        model.addAttribute("substationId", station.getCcId());
        model.addAttribute("substationName", substationName);
        model.addAttribute("subBusName", subBusName);
    }
    
    private void setupZoneList(ModelMap model, CapControlCache cache, int subBusId) {
        ZoneHierarchy hierarchy = zoneService.getZoneHierarchyBySubBusId(subBusId);
        model.addAttribute("zones",hierarchy);
        Map<String, Phase> phaseMap = Maps.newHashMapWithExpectedSize(3);
        for (Phase phase : Phase.getRealPhases()) {
            phaseMap.put(phase.name(), phase);
        }
        model.addAttribute("phaseMap", phaseMap);
        
        //Check for any unassigned banks and flag it
        List<Integer> unassignedBankIds = zoneService.getUnassignedCapBankIdsForSubBusId(subBusId);
        
        model.addAttribute("unassignedBanksExist",unassignedBankIds.size()>0);

        List<ZoneVoltagePointsHolder> zoneVoltagePointsHolders = Lists.newArrayList();
        setupZoneVoltagePoints(zoneVoltagePointsHolders, hierarchy);
        model.addAttribute("zoneVoltagePointsHolders", zoneVoltagePointsHolders);
    }
    
    private void setupZoneVoltagePoints(List<ZoneVoltagePointsHolder> zoneVoltagePointsHolders, ZoneHierarchy hierarchy) {
        if(hierarchy == null) return; //no zones exist for this sub bus
        AbstractZone zone = hierarchy.getZone();
        List<VoltageLimitedDeviceInfo> voltageInfos = ccMonitorBankListDao.getDeviceInfoByZoneId(zone.getZoneId());
        ZoneVoltagePointsHolder pointsHolder = new ZoneVoltagePointsHolder(zone.getZoneId(), voltageInfos);
        pointsHolder.setZoneName(zone.getName());
        zoneVoltagePointsHolders.add(pointsHolder);
        
        for (ZoneHierarchy childHierarchy : hierarchy.getChildren()) {
            // Recursion... GO!!
            setupZoneVoltagePoints(zoneVoltagePointsHolders, childHierarchy);
        }
    }
}

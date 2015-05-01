package com.cannontech.web.capcontrol.ivvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.StrategyLimitsHolder;
import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.capcontrol.model.ZoneHierarchy;
import com.cannontech.capcontrol.model.ZoneVoltagePointsHolder;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.common.chart.service.FlotChartService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RequestMapping("/ivvc/bus/*")
@Controller
public class BusViewController {
    
    @Autowired private AttributeService attrService;
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ZoneService zoneService;
    @Autowired private StrategyDao strategyDao;
    @Autowired private VoltageFlatnessGraphService voltageFlatness;
    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;
    @Autowired private FlotChartService flotChartService;
    
    @RequestMapping(value="detail", method = RequestMethod.GET)
    public String detail(ModelMap model, YukonUserContext userContext, int subBusId) {
        
        setupDetails(model, userContext, subBusId);
        
        return "ivvc/busView.jsp";
    }
    
    private void setupDetails(ModelMap model, YukonUserContext userContext, int subBusId) {
        
        LiteYukonUser user = userContext.getYukonUser();
        model.addAttribute("subBusId", subBusId);
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole", hasEditingRole);
        
        boolean hasSubBusControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_SUBBUS_CONTROLS, user);
        model.addAttribute("hasSubBusControl", hasSubBusControl);
        
        LiteYukonPAObject bus = dbCache.getAllPaosMap().get(subBusId);
        LitePoint pfPoint = attrService.getPointForAttribute(bus, BuiltInAttribute.POWER_FACTOR);
        model.addAttribute("pfPoint", pfPoint);
        
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        setupBreadCrumbs(model, cache, subBusId, userContext);
        setupZoneList(model, cache, subBusId);
        setupStrategyDetails(model, cache, subBusId);
        
        setupChart(model, userContext, subBusId);
        
        model.addAttribute("gangOperatedZone", ZoneType.GANG_OPERATED);
        model.addAttribute("threePhaseOperatedZone", ZoneType.THREE_PHASE);
        model.addAttribute("singlePhaseZone", ZoneType.SINGLE_PHASE);
    }
    
    @RequestMapping(value="chart", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> chart(YukonUserContext userContext, int subBusId) {
        
        boolean zoneAttributesExist = voltageFlatness.
                allZonesHaveRequiredRegulatorPointMapping(subBusId, userContext.getYukonUser());
        
        if (zoneAttributesExist) {
            VfGraph graph = voltageFlatness.getSubBusGraph(userContext, subBusId);
            Map<String, Object> graphAsJson = 
                    flotChartService.getIVVCGraphData(graph, graph.getSettings().isShowZoneTransitionTextZoneGraph());
            return graphAsJson;
        }
        
        return Collections.emptyMap();
    }
    
    private VfGraph setupChart(ModelMap model, YukonUserContext userContext, int subBusId) {
        
        boolean zoneAttributesExist = voltageFlatness.
                allZonesHaveRequiredRegulatorPointMapping(subBusId, userContext.getYukonUser());
        
        VfGraph graph = null;
        if (zoneAttributesExist) {
            graph = voltageFlatness.getSubBusGraph(userContext, subBusId);
            Map<String, Object> graphAsJSON = 
                    flotChartService.getIVVCGraphData(graph, graph.getSettings().isShowZoneTransitionTextBusGraph());
            model.addAttribute("graphAsJSON", graphAsJSON);
            model.addAttribute("graph", graph);
            model.addAttribute("graphSettings", graph.getSettings());
        }
        
        return graph;
    }
    
    private void setupStrategyDetails(ModelMap model, CapControlCache cache, int subBusId) {
        
        int strategyId = cache.getSubBus(subBusId).getStrategyId();
        StrategyLimitsHolder strategyLimitsHolder = strategyDao.getStrategyLimitsHolder(strategyId);
        
        model.addAttribute("strategyLimits", strategyLimitsHolder);
        model.addAttribute("strategyId", strategyLimitsHolder.getStrategy().getStrategyID());
        model.addAttribute("strategyName", strategyLimitsHolder.getStrategy().getStrategyName());
        model.addAttribute("strategySettings", strategyLimitsHolder.getStrategy().getTargetSettings());
    }
    
    private void setupBreadCrumbs(ModelMap model, CapControlCache cache, int subBusId, YukonUserContext userContext) {
        
        StreamableCapObject subBus = cache.getSubBus(subBusId);
        SubStation station = cache.getSubstation(subBus.getParentID());
        
        StreamableCapObject area = cache.getStreamableArea(station.getParentID());
        
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
        
        // Check for any unassigned banks and flag it
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
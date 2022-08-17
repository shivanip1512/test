package com.cannontech.web.capcontrol.ivvc;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.capcontrol.ControlAlgorithm;
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
import com.cannontech.common.util.TimeRange;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.database.db.capcontrol.StrategyPeakSettingsHelper;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.IvvcHelper;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.common.chart.service.FlotChartService;
import com.cannontech.web.user.service.UserPreferenceService;
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
    @Autowired private UserPreferenceService userPreferenceService;
    @Autowired private IvvcHelper ivvcHelper;
    
    @RequestMapping(value="detail", method = RequestMethod.GET)
    public String detail(ModelMap model, YukonUserContext userContext, int subBusId, HttpServletRequest req) throws IOException {
        
        setupDetails(model, userContext, subBusId, req);
        
        return "ivvc/busView.jsp";
    }
    
    private void setupDetails(ModelMap model, YukonUserContext userContext, int subBusId, HttpServletRequest req) throws IOException {
        
        LiteYukonUser user = userContext.getYukonUser();
        model.addAttribute("subBusId", subBusId);
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole", hasEditingRole);
        
        LiteYukonPAObject bus = dbCache.getAllPaosMap().get(subBusId);
        LitePoint pfPoint = attrService.getPointForAttribute(bus, BuiltInAttribute.POWER_FACTOR);
        model.addAttribute("pfPoint", pfPoint);
        
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        setupBreadCrumbs(model, cache, subBusId);
        setupZoneList(model, subBusId);
        setupStrategyDetails(model, cache, subBusId);
        
        setupChart(model, userContext, subBusId);
        
        model.addAttribute("gangOperatedZone", ZoneType.GANG_OPERATED);
        model.addAttribute("threePhaseOperatedZone", ZoneType.THREE_PHASE);
        model.addAttribute("singlePhaseZone", ZoneType.SINGLE_PHASE);
        
        model.addAttribute("ranges", TimeRange.values());
        TimeRange lastRange =
            TimeRange.valueOf(userPreferenceService.getPreference(userContext.getYukonUser(),
                UserPreferenceName.DISPLAY_EVENT_RANGE));
        model.addAttribute("lastRange", lastRange);
        Map<String, Integer> hours = new HashMap<>();
        for (TimeRange range : TimeRange.values()) {
            hours.put(range.name(), range.getHours());
        }
        model.put("hours", hours);

    }
    
    @RequestMapping(value="chart", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> chart(YukonUserContext userContext, int subBusId) {
        
        boolean zoneAttributesExist = voltageFlatness.
                allZonesHaveRequiredRegulatorPointMapping(subBusId, userContext.getYukonUser());
        
        if (zoneAttributesExist) {
            VfGraph graph = voltageFlatness.getSubBusGraph(userContext, subBusId);
            Map<String, Object> graphAsJson = 
                    flotChartService.getIVVCGraphData(graph, graph.getSettings().isShowZoneTransitionTextBusGraph());
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
        model.addAttribute("strategyId", strategyLimitsHolder.getStrategy().getId());
        model.addAttribute("strategyName", strategyLimitsHolder.getStrategy().getName());
        model.addAttribute("strategy", strategyLimitsHolder.getStrategy());
        ControlAlgorithm controlAlgorithm = strategyLimitsHolder.getStrategy().getAlgorithm();
        List<TargetSettingType> targetSettingTypes = StrategyPeakSettingsHelper.getAlgorithmToSettings().get(controlAlgorithm);
        model.addAttribute("targetSettingTypes",  targetSettingTypes);
    }
    
    private void setupBreadCrumbs(ModelMap model, CapControlCache cache, int subBusId) {
        
        StreamableCapObject subBus = cache.getSubBus(subBusId);
        SubStation station = cache.getSubstation(subBus.getParentID());
        
        StreamableCapObject area = cache.getStreamableArea(station.getParentID());
        
        String areaName = area.getCcName();
        String substationName = station.getCcName();
        String subBusName = subBus.getCcName();
        
        model.addAttribute("areaId", area.getCcId());
        model.addAttribute("areaName", areaName);
        model.addAttribute("substationId", station.getCcId());
        model.addAttribute("substationName", substationName);
        model.addAttribute("subBusName", subBusName);
    }
    
    private void setupZoneList(ModelMap model, int subBusId) {
        
        ZoneHierarchy hierarchy = zoneService.getZoneHierarchyBySubBusId(subBusId);
        model.addAttribute("zones",hierarchy);
        
        Map<String, Phase> phaseMap = Maps.newHashMapWithExpectedSize(3);
        for (Phase phase : Phase.getRealPhases()) {
            phaseMap.put(phase.name(), phase);
        }
        model.addAttribute("phaseMap", phaseMap);
        
        // Check for any unassigned banks and flag it
        List<Integer> unassignedBankIds = zoneService.getUnassignedCapBankIdsForSubBusId(subBusId);
        
        model.addAttribute("unassignedBanksCount", unassignedBankIds.size());
        
        List<ZoneVoltagePointsHolder> zoneVoltagePointsHolders = Lists.newArrayList();
        setupZoneVoltagePoints(zoneVoltagePointsHolders, hierarchy);
        model.addAttribute("zoneVoltagePointsHolders", zoneVoltagePointsHolders);
    }
    
    private void setupZoneVoltagePoints(List<ZoneVoltagePointsHolder> zoneVoltagePointsHolders, ZoneHierarchy hierarchy) {
        
        if(hierarchy == null) return; //no zones exist for this sub bus
        AbstractZone zone = hierarchy.getZone();
        List<VoltageLimitedDeviceInfo> voltageInfos = ccMonitorBankListDao.getDeviceInfoByZoneId(zone.getZoneId());
        ZoneVoltagePointsHolder pointsHolder = new ZoneVoltagePointsHolder(zone.getZoneId(), voltageInfos);
        ivvcHelper.setIgnoreFlagForPoints(pointsHolder);
        pointsHolder.setZoneName(zone.getName());
        zoneVoltagePointsHolders.add(pointsHolder);
        
        for (ZoneHierarchy childHierarchy : hierarchy.getChildren()) {
            // Recursion... GO!!
            setupZoneVoltagePoints(zoneVoltagePointsHolders, childHierarchy);
        }
    }
    
}
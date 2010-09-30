package com.cannontech.web.capcontrol.ivvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.service.LtcService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.model.Zone;
import com.cannontech.cbc.web.CapControlCommandExecutor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.DynamicDataDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.capcontrol.LtcPointMapping;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.capcontrol.ivvc.models.VfGraphData;
import com.cannontech.web.capcontrol.ivvc.models.VfGraphSettings;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.capcontrol.ivvc.service.ZoneService;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.util.CapControlWebUtils;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Ltc;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubStation;
import com.google.common.collect.Lists;


@RequestMapping("/ivvc/zone/*")
@Controller
public class ZoneDetailController {

    private FilterCacheFactory filterCacheFactory;
    private RolePropertyDao rolePropertyDao;
    private ZoneService zoneService;
    private DynamicDataDao dynamicDataDao;
    private LtcService ltcService;
    private AttributeService attributeService;
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
        Ltc ltc = cache.getLtc(zone.getRegulatorId());
        
        setupDetails(model,ltc);
        setupIvvcEvents(model);
        setupCapBanks(model,cache,zone);
        setupBreadCrumbs(model, cache, zone, isSpecialArea);
        setupDeltas(model,request,cache,zone);
        setupLtcPointList(model,ltc.getCcId());
        
        model.addAttribute("subBusId", zone.getSubstationBusId());
        
        return "ivvc/zoneDetail.jsp";
    }
    
    @RequestMapping
    public String deltaUpdate(ModelMap model, int bankId, int pointId, double delta, int zoneId, Boolean isSpecialArea, LiteYukonUser user, HttpServletRequest request) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        CapControlCommandExecutor exec = new CapControlCommandExecutor(cache, user);
        
        exec.executeDeltaUpdate(bankId,pointId,delta);
        
        if(isSpecialArea == null) {
            isSpecialArea = false;
        }
        
        model.addAttribute("isSpecialArea", isSpecialArea);
        model.addAttribute("zoneId", zoneId);
        return "redirect:/spring/capcontrol/ivvc/zone/detail";
    }
    
    @RequestMapping
    public String chartData(ModelMap model, LiteYukonUser user, int zoneId) {
        VfGraphData graph = voltageFlatnessGraphService.getZoneGraphData(user, zoneId);
        
        model.addAttribute("graph", graph);
        
        return "ivvc/flatnessGraphData.jsp";
    }
    
    @RequestMapping
    public String chartSettings(ModelMap model, LiteYukonUser user, int zoneId) {
        VfGraphSettings graph = voltageFlatnessGraphService.getZoneGraphSettings(user, zoneId);
        
        model.addAttribute("graph", graph);
        
        return "ivvc/flatnessGraphSettings.jsp";
    }
    
    private void setupDetails(ModelMap model, Ltc ltc) {
        model.addAttribute("ltcId",ltc.getCcId());
        model.addAttribute("ltcName",ltc.getCcName());
        
        LtcPointMapping voltage = ltcService.getLtcPointMapping(ltc.getCcId(), BuiltInAttribute.VOLTAGE);
        LtcPointMapping tapPosition = ltcService.getLtcPointMapping(ltc.getCcId(), BuiltInAttribute.TAP_POSITION);
        LtcPointMapping localAuto = ltcService.getLtcPointMapping(ltc.getCcId(), BuiltInAttribute.AUTO_REMOTE_CONTROL);
        
        model.addAttribute("voltageMapping", voltage);
        model.addAttribute("tapPositionMapping", tapPosition);
        model.addAttribute("localAutoMapping", localAuto);
    }
    
    private void setupIvvcEvents(ModelMap model) {
        
        return;
    }
    
    private void setupCapBanks(ModelMap model, CapControlCache cache, Zone zone) {
        List<Integer> capBankIdList = zoneService.getCapBankIdsForZoneId(zone.getId());
        
        List<CapBankDevice> capBankList = Lists.newArrayList();
        for (Integer bankId : capBankIdList) {
            CapBankDevice bank = cache.getCapBankDevice(bankId);
            capBankList.add(bank);
        }
        
        List<ViewableCapBank> viewableCapBankList = CapControlWebUtils.createViewableCapBank(capBankList);
        for (ViewableCapBank bank:viewableCapBankList) {
            LitePoint point = attributeService.getPointForAttribute(bank.getControlDevice(), BuiltInAttribute.VOLTAGE);
            
            bank.setVoltagePointId(point.getLiteID());
        }
        
        model.addAttribute("capBankList", viewableCapBankList);
    }
    
    private void setupLtcPointList(ModelMap model, int regulatorId) {
        List<LtcPointMapping> pointMappings = ltcService.getLtcPointMappings(regulatorId);
        Collections.sort(pointMappings);
        model.addAttribute("ltcPointMappings", pointMappings);
    }
    
    private void setupDeltas(ModelMap model, HttpServletRequest request, CapControlCache cache, Zone zone) {
        List<Integer> bankIds = new ArrayList<Integer>();
        
        List<CapBankDevice> capBanks = cache.getCapBanksBySubBus(zone.getSubstationBusId());
        
        for (CapBankDevice bank : capBanks) {
            bankIds.add(bank.getCcId());
        }
        
        List<CapBankPointDelta> pointDeltas = dynamicDataDao.getAllPointDeltasForBankIds(bankIds);
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 10);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = pointDeltas.size();
        
        if(numberOfResults < toIndex) toIndex = numberOfResults;
        pointDeltas = pointDeltas.subList(startIndex, toIndex);
        
        SearchResult<CapBankPointDelta> searchResults = new SearchResult<CapBankPointDelta>();
        searchResults.setResultList(pointDeltas);
        
        model.addAttribute("zoneId", zone.getId());
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("pointDeltas", searchResults.getResultList());
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
    public void setDynamicDataDao (DynamicDataDao dynamicDataDao) {
        this.dynamicDataDao = dynamicDataDao;
    }
    
    @Autowired
    public void setLtcService(LtcService ltcService) {
        this.ltcService = ltcService;
    }

    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setVoltageFlatnessGraphService(VoltageFlatnessGraphService voltageFlatnessGraphService) {
        this.voltageFlatnessGraphService = voltageFlatnessGraphService;
    }
}

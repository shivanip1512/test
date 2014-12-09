package com.cannontech.web.capcontrol;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/tier/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class AreaController {
    
    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private CapControlWebUtilsService capControlWebUtilsService;

    @RequestMapping("substations")
    public String substations(HttpServletRequest request, ModelMap model, LiteYukonUser user,
            @RequestParam("bc_areaId") int areaId) {

        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        
        boolean hasAreaControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_AREA_CONTROLS, user);
        model.addAttribute("hasAreaControl", hasAreaControl);
        
        boolean hasSubstationControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_SUBSTATION_CONTROLS, user);
        model.addAttribute("hasSubstationControl", hasSubstationControl);
        
        StreamableCapObject area = cache.getStreamableArea(areaId);
        
        String description = "";
        if (area instanceof Area) {
            Area normalArea = (Area) area;
            description = normalArea.getPaoDescription();
            model.addAttribute("type", "CBCAREA");
        } else if (area instanceof SpecialArea) {
            SpecialArea specialArea = (SpecialArea) area;
            description = specialArea.getPaoDescription();
            model.addAttribute("type", "CBCSPECIALAREA");
        }
        
        model.addAttribute("description", description);
        
        List<SubStation> substations = cache.getSubstationsByArea(areaId);
        
        List<SubBus> subBusList = cache.getSubBusesByArea(areaId);
        Collections.sort(subBusList, CapControlUtils.SUB_DISPLAY_COMPARATOR);
        List<ViewableSubBus> viewableSubBusList = capControlWebUtilsService.createViewableSubBus(subBusList);
        model.addAttribute("subBusList", viewableSubBusList);
        
        List<Feeder> feederList = cache.getFeedersByArea(areaId);
        List<ViewableFeeder> viewableFeederList = capControlWebUtilsService.createViewableFeeder(feederList);
        model.addAttribute("feederList", viewableFeederList);

        List<CapBankDevice> capBankList = cache.getCapBanksByArea(areaId);
        List<ViewableCapBank> viewableCapBankList = capControlWebUtilsService.createViewableCapBank(capBankList);
        model.addAttribute("capBankList", viewableCapBankList);
        
        boolean hideReports = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_REPORTS, user);
        boolean hideGraphs = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_GRAPHS, user);
        model.addAttribute("showAnalysis", !hideReports && !hideGraphs);
        
        model.addAttribute("bc_areaId", areaId);
        model.addAttribute("bc_areaName", area.getCcName());
        model.addAttribute("subStations", substations);
        model.addAttribute("subStationParam", CCSessionInfo.STR_SUBID);
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole", hasEditingRole);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        return "tier/area.jsp";
    }
    
}

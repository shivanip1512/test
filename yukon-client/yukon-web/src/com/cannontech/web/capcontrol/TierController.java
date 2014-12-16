package com.cannontech.web.capcontrol;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;


@Controller
@RequestMapping("/tier/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class TierController {

    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SubstationDao substationDao;
    @Autowired private CapControlWebUtilsService capControlWebUtilsService;

    private static final Logger log = YukonLogManager.getLogger(TierController.class);

    @RequestMapping("areas")
    public String areas(HttpServletRequest request, LiteYukonUser user, ModelMap model) {

        Instant startPage = Instant.now();

        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

        Map<AreaType, List<ViewableArea>> areasByType = new LinkedHashMap<>();

        List<ViewableArea> viewableAreas = capControlWebUtilsService.createViewableAreas(cache.getAreas(), cache, false);
        areasByType.put(AreaType.NORMAL, viewableAreas);

        List<ViewableArea> viewableSpecialAreas = capControlWebUtilsService.createViewableAreas(cache.getSpecialAreas(), cache, true);
        areasByType.put(AreaType.SPECIAL, viewableSpecialAreas);

        model.addAttribute("areasMap", areasByType);

        setUpAreas(model, user);

        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());

        long timeForPage = new Interval(startPage, Instant.now()).toDurationMillis();
        log.debug("Time to map dashboard: "  + timeForPage + "ms");

        return "tier/areaTier.jsp";
    }

    private final void setUpAreas(ModelMap model, LiteYukonUser user) {
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole", hasEditingRole);

        boolean hasAreaControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_AREA_CONTROLS, user);
        model.addAttribute("hasAreaControl", hasAreaControl);
        model.addAttribute("systemStatusCommandId", CommandType.SYSTEM_STATUS.getCommandId());
        model.addAttribute("resetOpCountCommandId", CommandType.RESET_SYSTEM_OP_COUNTS.getCommandId());
    }

    public enum AreaType {
        
        NORMAL("normal", "CBCAREA", false),
        SPECIAL("special", "CBCSPECIALAREA", true);

        private final String type;
        private final String updaterType;
        private final boolean isSpecialArea;

        AreaType(String type, String updaterType, boolean isSpecialArea){
            this.type = type;
            this.updaterType = updaterType;
            this.isSpecialArea = isSpecialArea;
        }

        public String getType(){
            return type;
        }

        public String getUpdaterType() {
            return updaterType;
        }

        public boolean isSpecialArea() {
            return isSpecialArea;
        }
    }

    @RequestMapping("feeders")
    public String feeders(HttpServletRequest request, 
            FlashScope flashScope, 
            ModelMap model,
            YukonUserContext userContext, 
            int substationId) {
        
        Instant startPage = Instant.now();

        LiteYukonUser user = userContext.getYukonUser();
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        SubStation cachedSubstation = cache.getSubstation(substationId);
        Substation substation = substationDao.findSubstationById(substationId);
        model.addAttribute("substation", substation);
        
        boolean hideReports = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_REPORTS, user);
        boolean hideGraphs = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_GRAPHS, user);
        model.addAttribute("showAnalysis", !hideReports && !hideGraphs);

        // Name of the area in the breadcrumb, will be the name of the special area if they came from the special areas page.
        Area area = cache.getArea(cachedSubstation.getParentID());
        
        model.addAttribute("bc_areaName", area.getCcName());
        model.addAttribute("bc_areaId", area.getCcId());
        model.addAttribute("substationName", substation.getName());
        model.addAttribute("substationId", substationId);
        model.addAttribute("areaId", cachedSubstation.getParentID());
        model.addAttribute("specialAreaId", cachedSubstation.getSpecialAreaId());
        
        List<SubBus> subBusList = cache.getSubBusesBySubStation(cachedSubstation);
        Collections.sort(subBusList, CapControlUtils.SUB_DISPLAY_COMPARATOR);
        List<ViewableSubBus> viewableSubBusList = capControlWebUtilsService.createViewableSubBus(subBusList);
        model.addAttribute("subBusList", viewableSubBusList);
        
        List<Feeder> feederList = cache.getFeedersBySubStation(cachedSubstation);
        List<ViewableFeeder> viewableFeederList = capControlWebUtilsService.createViewableFeeder(feederList);
        model.addAttribute("feederList", viewableFeederList);

        List<CapBankDevice> capBankList = cache.getCapBanksBySubStation(cachedSubstation);
        List<ViewableCapBank> viewableCapBankList = capControlWebUtilsService.createViewableCapBank(capBankList);
        model.addAttribute("capBankList", viewableCapBankList);

        boolean hasSubstationControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_SUBSTATION_CONTROLS, user);
        model.addAttribute("hasSubstationControl", hasSubstationControl);
        
        boolean hasSubBusControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_SUBBUS_CONTROLS, user);
        model.addAttribute("hasSubBusControl", hasSubBusControl);
        
        boolean hasFeederControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_FEEDER_CONTROLS, user);
        model.addAttribute("hasFeederControl", hasFeederControl);
        
        boolean hasCapbankControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CAPBANK_CONTROLS, user);
        model.addAttribute("hasCapbankControl", hasCapbankControl);
        
        boolean showFlip = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_FLIP_COMMAND, user);
        model.addAttribute("showFlip", showFlip);
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole", hasEditingRole);
        
        for (ViewableSubBus bus : viewableSubBusList) {
            if (!CapControlUtils.isStrategyAttachedToSubBusOrSubBusParentArea(cache.getSubBus(bus.getCcId()))) {
                SubStation subBusSubstation = cache.getSubstation(cache.getSubBus(bus.getCcId()).getParentID());
                int parentAreaId;
                if (subBusSubstation.getSpecialAreaEnabled()) {
                    parentAreaId = subBusSubstation.getSpecialAreaId();
                } else {
                    parentAreaId = cache.getParentAreaId(bus.getCcId());
                }
                StreamableCapObject streamable = cache.getStreamableArea(parentAreaId);

                String areaLinkHtml = capControlWebUtilsService.getCapControlFacesEditorLinkHtml(request,
                    streamable.getCcId());
                String subBusLinkHtml = capControlWebUtilsService.getCapControlFacesEditorLinkHtml(request,
                    bus.getCcId());
                areaLinkHtml = "<strong>" + areaLinkHtml + "</strong>";
                subBusLinkHtml = "<strong>" + subBusLinkHtml + "</strong>";
                WebMessageSourceResolvable noStrategyMessage =
                        new WebMessageSourceResolvable("yukon.web.modules.capcontrol.substation.noStrategyAssigned",
                            areaLinkHtml, subBusLinkHtml);
                flashScope.setError(noStrategyMessage);
            }
        }

        //Security risk? They can change the link if they intercept
        String url = request.getRequestURL().toString();
        String urlParams = request.getQueryString();
        String fullURL = url + ((urlParams != null) ? "?" + urlParams : "");
        
        model.addAttribute("fullURL", fullURL);
        
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        long timeForPage = new Interval(startPage, Instant.now()).toDurationMillis();
        log.debug("Time to map substation: "  + timeForPage + "ms");

        return "tier/substation.jsp";
    }
    
    @RequestMapping(value="updateSession", method=RequestMethod.POST)
    public void updateSession(HttpServletRequest request, CCSessionInfo info) throws ServletException, Exception {
        if(info != null) {
            info.updateState( request );
        }
    }
    
}
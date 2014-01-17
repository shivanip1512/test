package com.cannontech.web.capcontrol;

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
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
	
	@RequestMapping("areas")
	public String areas(HttpServletRequest request, LiteYukonUser user, Boolean isSpecialArea, ModelMap model) {
		if (isSpecialArea == null) {
			isSpecialArea = false;
		}
		
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

		model.addAttribute("areaParam", CCSessionInfo.STR_CC_AREAID);
		model.addAttribute("isSpecialArea",isSpecialArea);
		List<? extends StreamableCapObject> ccAreas = null;
		
		if (isSpecialArea) {
			ccAreas = cache.getSpecialCbcAreas();
			model.addAttribute("updaterType", "CBCSPECIALAREA");
			model.addAttribute("type", "special");
		} else {
			ccAreas = cache.getCbcAreas();
			model.addAttribute("updaterType", "CBCAREA");
			model.addAttribute("type", "normal");
		}
		
		List<ViewableArea> viewableAreas = capControlWebUtilsService.createViewableAreas(ccAreas, cache, isSpecialArea);
		model.addAttribute("ccAreas", viewableAreas);
		
		boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole", hasEditingRole);
        
        boolean hasAreaControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_AREA_CONTROLS, user);
        model.addAttribute("hasAreaControl", hasAreaControl);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        model.addAttribute("systemStatusCommandId", CommandType.SYSTEM_STATUS.getCommandId());
        model.addAttribute("resetOpCountCommandId", CommandType.RESET_SYSTEM_OP_COUNTS.getCommandId());
        
		return "tier/areaTier.jsp";
    }
		
	@RequestMapping("substations")
	public String substations(HttpServletRequest request, 
	                          HttpSession session, 
	                          ModelMap model,
	                          LiteYukonUser user, 
			                  @RequestParam("bc_areaId") int areaId,
			                  Boolean isSpecialArea) {
	    
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
		
		if (isSpecialArea == null) {
			isSpecialArea = false;
		}

		boolean hasAreaControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_AREA_CONTROLS, user);
        model.addAttribute("hasAreaControl", hasAreaControl);
		
		boolean hasSubstationControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_SUBSTATION_CONTROLS, user);
		model.addAttribute("hasSubstationControl", hasSubstationControl);
	    
		StreamableCapObject area = cache.getArea(areaId);
		
		String description = "";
		if(area instanceof Area){
		    Area normalArea = (Area) area;
		    description = normalArea.getPaoDescription();
		    
		} else if(area instanceof SpecialArea){
		    SpecialArea specialArea = (SpecialArea) area;
            description = specialArea.getPaoDescription();
		}
		
		model.addAttribute("description", description);
		
	    List<SubStation> subStations = null;
	    if (isSpecialArea) {
	    	subStations = cache.getSubstationsBySpecialArea(areaId);
	    } else {
	    	subStations = cache.getSubstationsByArea(areaId);
	    }
	    
	    List<SubBus> subBusList = cache.getSubBusesByArea(areaId);
        Collections.sort(subBusList, CapControlUtils.SUB_DISPLAY_COMPARATOR);
        List<ViewableSubBus> viewableSubBusList = capControlWebUtilsService.createViewableSubBus(subBusList);
        model.addAttribute("subBusList", viewableSubBusList);
        
        List<Feeder> feederList = cache.getFeedersByArea(areaId);
        List<ViewableFeeder> viewableFeederList = capControlWebUtilsService.createViewableFeeder(feederList,cache);
        model.addAttribute("feederList", viewableFeederList);

        List<CapBankDevice> capBankList = cache.getCapBanksByArea(areaId);
        List<ViewableCapBank> viewableCapBankList = capControlWebUtilsService.createViewableCapBank(capBankList);
        model.addAttribute("capBankList", viewableCapBankList);
        
        boolean hideReports = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_REPORTS, user);
        boolean hideGraphs = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_GRAPHS, user);
        model.addAttribute("showAnalysis", !hideReports && !hideGraphs);
	    
	    model.addAttribute("bc_areaId", areaId);
	    model.addAttribute("bc_areaName", area.getCcName());
	    model.addAttribute("isSpecialArea", isSpecialArea);
	    model.addAttribute("subStations", subStations);
	    model.addAttribute("subStationParam", CCSessionInfo.STR_SUBID);
	    
	    boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole", hasEditingRole);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
	    
		return "tier/substationTier.jsp";
    }
	
	@RequestMapping("feeders")
	public String feeders(HttpServletRequest request, 
	                      FlashScope flashScope,
	                      HttpSession session,
	                      ModelMap model,
	                      YukonUserContext userContext,
	                      LiteYukonUser user, 
						  int substationId, 
						  Boolean isSpecialArea) {
	    
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
		SubStation cachedSubstation = cache.getSubstation(substationId);
		Substation substation = substationDao.findSubstationById(substationId);
		model.addAttribute("substation", substation);
		
		boolean hideReports = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_REPORTS, user);
		boolean hideGraphs = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_GRAPHS, user);
		model.addAttribute("showAnalysis", !hideReports && !hideGraphs);

		// Name of the area in the breadcrumb, will be the name of the special area if they came from the special areas page.
		String bc_areaName = "";
		int bc_areaId;
		if (isSpecialArea != null && isSpecialArea) {
		    SpecialArea specialArea = cache.getCBCSpecialArea(cachedSubstation.getSpecialAreaId());
            bc_areaName = specialArea.getCcName();
		    bc_areaId = specialArea.getCcId();
		} else {
		    Area area = cache.getCBCArea(cachedSubstation.getParentID());
            bc_areaName = area.getCcName();
		    bc_areaId = area.getCcId();
		}
		model.addAttribute("bc_areaName", bc_areaName);
		model.addAttribute("bc_areaId", bc_areaId);
		model.addAttribute("substationName", substation.getName());
		model.addAttribute("substationId", substationId);
		model.addAttribute("areaId", cachedSubstation.getParentID());
		model.addAttribute("specialAreaId", cachedSubstation.getSpecialAreaId());
		model.addAttribute("isSpecialArea", isSpecialArea);
		
		List<SubBus> subBusList = cache.getSubBusesBySubStation(cachedSubstation);
		Collections.sort(subBusList, CapControlUtils.SUB_DISPLAY_COMPARATOR);
		List<ViewableSubBus> viewableSubBusList = capControlWebUtilsService.createViewableSubBus(subBusList);
		model.addAttribute("subBusList", viewableSubBusList);
		
		List<Feeder> feederList = cache.getFeedersBySubStation(cachedSubstation);
		List<ViewableFeeder> viewableFeederList = capControlWebUtilsService.createViewableFeeder(feederList,cache);
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
		
		boolean hideOneLine = rolePropertyDao.checkProperty(YukonRoleProperty.HIDE_ONELINE, user);
		model.addAttribute("hideOneLine", hideOneLine);
		
		boolean showFlip = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_FLIP_COMMAND, user);
		model.addAttribute("showFlip", showFlip);
		
		boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
		model.addAttribute("hasEditingRole", hasEditingRole);
		
		for (ViewableSubBus subbus : viewableSubBusList) {
	        if (!CapControlUtils.isStrategyAttachedToSubBusOrSubBusParentArea(subbus.getSubBus())) {
	            SubStation subBusSubstation = cache.getSubstation(subbus.getSubBus().getParentID());
	            int parentAreaId;
	            if (subBusSubstation.getSpecialAreaEnabled()) {
	                parentAreaId = subBusSubstation.getSpecialAreaId();
	            } else {
	                parentAreaId = cache.getParentAreaID(subbus.getSubBus().getCcId());
	            }
	            StreamableCapObject area = cache.getArea(parentAreaId);

	            String areaLinkHtml = capControlWebUtilsService.getCapControlFacesEditorLinkHtml(area.getCcId(), userContext);
	            String subBusLinkHtml = capControlWebUtilsService.getCapControlFacesEditorLinkHtml(subbus.getSubBus().getCcId(), userContext);
	            areaLinkHtml = "<strong>" + areaLinkHtml + "</strong>";
	            subBusLinkHtml = "<strong>" + subBusLinkHtml + "</strong>";
	            WebMessageSourceResolvable noStrategyMessage = new WebMessageSourceResolvable("yukon.web.modules.capcontrol.substation.noStrategyAssigned", areaLinkHtml, subBusLinkHtml);
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
	    
		return "tier/substation.jsp";
    }
	
	@RequestMapping(value="updateSession", method=RequestMethod.POST)
	public void updateSession(HttpServletRequest request, CCSessionInfo info) throws ServletException, Exception {
		if(info != null) {
			info.updateState( request );
		}
	}
	
}
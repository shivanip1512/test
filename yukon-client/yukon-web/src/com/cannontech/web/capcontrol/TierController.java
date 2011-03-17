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

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.cbc.web.CCSessionInfo;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.util.CapControlWebUtils;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;


@Controller
@RequestMapping("/tier/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class TierController {

	private FilterCacheFactory filterCacheFactory;
	private RolePropertyDao rolePropertyDao;
	
	@RequestMapping
	public String areas(HttpServletRequest request, LiteYukonUser user, Boolean isSpecialArea, ModelMap mav) {
		if(isSpecialArea == null) {
			isSpecialArea = false;
		}
		
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

		mav.addAttribute("areaParam", CCSessionInfo.STR_CC_AREAID);
		mav.addAttribute("isSpecialArea",isSpecialArea);
		List<? extends StreamableCapObject> ccAreas = null;
		
		if(isSpecialArea) {
			ccAreas = cache.getSpecialCbcAreas();
			mav.addAttribute("title", "Special Substation Areas");
			mav.addAttribute("updaterType", "CBCSPECIALAREA");
		} else {
			ccAreas = cache.getCbcAreas();
			mav.addAttribute("title", "Substation Areas");
			mav.addAttribute("updaterType", "CBCAREA");
		}
		
		List<ViewableArea> viewableAreas = CapControlWebUtils.createViewableAreas(ccAreas, cache, isSpecialArea);
		mav.addAttribute("ccAreas", viewableAreas);
		
		boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        mav.addAttribute("hasEditingRole", hasEditingRole);
        
        boolean hasAreaControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_AREA_CONTROLS, user);
        mav.addAttribute("hasAreaControl", hasAreaControl);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
		return "tier/areaTier.jsp";
    }
		
	@RequestMapping
	public String substations(HttpServletRequest request, HttpSession session, LiteYukonUser user, 
			                  int areaId, Boolean isSpecialArea, ModelMap mav) {
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
		
		if(isSpecialArea == null) {
			isSpecialArea = false;
		}
		
		//2 fail points. Area does not exist. and No Access. 
		//add redirect here if they fail
		
		boolean hasSubstationControl = CBCWebUtils.hasSubstationControlRights(session);
	    
		StreamableCapObject area = cache.getArea(areaId);
		
		String containerTitle = "Substation In Area:  " + area.getCcName();
	    String mainTitle = area.getCcName() + " - Substations";
	    	    
	    List<SubStation> subStations = null;
	    if(isSpecialArea) {
	    	subStations = cache.getSubstationsBySpecialArea(areaId);
	    } else {
	    	subStations = cache.getSubstationsByArea(areaId);
	    }
	    
	    mav.addAttribute("areaId", areaId);
	    mav.addAttribute("areaName", area.getCcName());
	    mav.addAttribute("isSpecialArea", isSpecialArea);
	    mav.addAttribute("hasSubstationControl", hasSubstationControl);
	    mav.addAttribute("containerTitle", containerTitle);
	    mav.addAttribute("mainTitle", mainTitle);
	    mav.addAttribute("subStations", subStations);
	    mav.addAttribute("subStationParam", CCSessionInfo.STR_SUBID);
	    
	    boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        mav.addAttribute("hasEditingRole", hasEditingRole);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
	    
		return "tier/substationTier.jsp";
    }
	
	@RequestMapping
	public String feeders(HttpServletRequest request, HttpSession session, LiteYukonUser user, 
						  Integer areaId, Integer subStationId, Boolean isSpecialArea, ModelMap mav) {
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
		SubStation substation = cache.getSubstation(subStationId);
		mav.addAttribute("substation", substation);

		StreamableCapObject area;
		if(isSpecialArea) {
			area = cache.getCBCSpecialArea(areaId);
		} else {
			area = cache.getCBCArea(areaId);	
		}
		mav.addAttribute("areaName", area.getCcName());
		mav.addAttribute("subStationId", subStationId);
		mav.addAttribute("areaId", areaId);
		mav.addAttribute("isSpecialArea",isSpecialArea);
		//2 fail points. Station does not exist. and No Access. 
		//add redirect here if they fail
		
		List<SubBus> subBusList = cache.getSubBusesBySubStation(substation);
		Collections.sort(subBusList, CBCUtils.SUB_DISPLAY_COMPARATOR);
		List<ViewableSubBus> viewableSubBusList = CapControlWebUtils.createViewableSubBus(subBusList);
		mav.addAttribute("subBusList", viewableSubBusList);
		
		List<Feeder> feederList = cache.getFeedersBySubStation(substation);
		List<ViewableFeeder> viewableFeederList = CapControlWebUtils.createViewableFeeder(feederList,cache);
		mav.addAttribute("feederList", viewableFeederList);

		List<CapBankDevice> capBankList = cache.getCapBanksBySubStation(substation);
		List<ViewableCapBank> viewableCapBankList = CapControlWebUtils.createViewableCapBank(capBankList);
		mav.addAttribute("capBankList", viewableCapBankList);

		boolean hasSubstationControl = CBCWebUtils.hasSubstationControlRights(session);
		mav.addAttribute("hasSubstationControl",hasSubstationControl);
		
		boolean hasSubBusControl = CBCWebUtils.hasSubstationBusControlRights(session);
		mav.addAttribute("hasSubBusControl", hasSubBusControl);
		
		boolean hasFeederControl = CBCWebUtils.hasFeederControlRights(session);
		mav.addAttribute("hasFeederControl", hasFeederControl);
		
		boolean hasCapbankControl = CBCWebUtils.hasCapbankControlRights(session);
		mav.addAttribute("hasCapbankControl", hasCapbankControl);
		
		boolean hideOneLine = rolePropertyDao.checkProperty(YukonRoleProperty.HIDE_ONELINE, user);
		mav.addAttribute("hideOneLine", hideOneLine);
		
		boolean showFlip = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_FLIP_COMMAND, user);
		mav.addAttribute("showFlip", showFlip);
		
		boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
		mav.addAttribute("hasEditingRole", hasEditingRole);

	    //Security risk? They can change the link if they intercept
	    String url = request.getRequestURL().toString();
	    String urlParams = request.getQueryString();
	    String fullURL = url + ((urlParams != null) ? "?" + urlParams : "");
	    
	    mav.addAttribute("fullURL", fullURL);
	    
	    String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
	    CBCNavigationUtil.setNavigation(requestURI , request.getSession());
	    
		return "tier/feederTier.jsp";
    }
	
	@RequestMapping(method=RequestMethod.POST)
	public void updateSession(HttpServletRequest request, CCSessionInfo info) throws ServletException, Exception {
		if(info != null) {
			info.updateState( request );
		}
	}
	
	@Autowired
	public void setFilteredCapControlcache (FilterCacheFactory factory) {
		this.filterCacheFactory = factory;
	}
	
	@Autowired
    public void setRolePropertyDao (RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
	
}

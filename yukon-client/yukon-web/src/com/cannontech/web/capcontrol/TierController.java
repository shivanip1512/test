package com.cannontech.web.capcontrol;

import java.util.ArrayList;
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
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.models.ViewableSubStation;
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
	private AuthDao authDao;
	private PointDao pointDao;
	private PaoDao paoDao;
	
	@RequestMapping
	public String areas(HttpServletRequest request, LiteYukonUser user, Boolean isSpecialArea, ModelMap mav) {
		if(isSpecialArea == null) {
			isSpecialArea = new Boolean(false);
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
		
		List<ViewableArea> viewableAreas = createViewableArea(ccAreas, cache, isSpecialArea);
		mav.addAttribute("ccAreas", viewableAreas);

		return "tier/areaTier";
    }
		
	@RequestMapping
	public String substations(HttpServletRequest request, HttpSession session, LiteYukonUser user, 
			                  Integer areaId, Boolean isSpecialArea, ModelMap mav) {
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

		//2 fail points. Area does not exist. and No Access. 
		//add redirect here if they fail
		
		String popupEvent = authDao.getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
		if (popupEvent == null) {
			popupEvent = "onmouseover";
		}
		
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
	    mav.addAttribute("popupEvent", popupEvent);
	    mav.addAttribute("hasSubstationControl", hasSubstationControl);
	    mav.addAttribute("containerTitle", containerTitle);
	    mav.addAttribute("mainTitle", mainTitle);
	    mav.addAttribute("subStations", subStations);
	    mav.addAttribute("subStationParam", CCSessionInfo.STR_SUBID);
	    
		return "tier/substationTier";
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
		List<ViewableSubBus> viewableSubBusList = createViewableSubBus(subBusList);
		mav.addAttribute("subBusList", viewableSubBusList);
		
		List<Feeder> feederList = cache.getFeedersBySubStation(substation);
		List<ViewableFeeder> viewableFeederList = createViewableFeeder(feederList,cache);
		mav.addAttribute("feederList", viewableFeederList);

		List<CapBankDevice> capBankList = cache.getCapBanksBySubStation(substation);
		List<ViewableCapBank> viewableCapBankList = createViewableCapBank(capBankList);
		mav.addAttribute("capBankList", viewableCapBankList);

		boolean hasSubstationControl = CBCWebUtils.hasSubstationControlRights(session);
		mav.addAttribute("hasSubstationControl",hasSubstationControl);
		
		boolean hasSubBusControl = CBCWebUtils.hasSubstationBusControlRights(session);
		mav.addAttribute("hasSubBusControl", hasSubBusControl);
		
		boolean hasFeederControl = CBCWebUtils.hasFeederControlRights(session);
		mav.addAttribute("hasFeederControl", hasFeederControl);
		
		boolean hasCapbankControl = CBCWebUtils.hasCapbankControlRights(session);
		mav.addAttribute("hasCapbankControl", hasCapbankControl);
		
		boolean hideOneLine = Boolean.valueOf(authDao.getRolePropertyValue(user, CBCSettingsRole.HIDE_ONELINE));
		mav.addAttribute("hideOneLine", hideOneLine);
		
		boolean showFlip = Boolean.valueOf(authDao.getRolePropertyValue(user, CBCSettingsRole.SHOW_FLIP_COMMAND)).booleanValue();
		mav.addAttribute("showFlip", showFlip);
		
		String popupEvent = authDao.getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
		if (popupEvent == null) { 
			popupEvent = "onmouseover";
		}		
		mav.addAttribute("popupEvent", popupEvent);
	    

	    //Security risk? They can change the link if they intercept
	    String url = request.getRequestURL().toString();
	    String urlParams = request.getQueryString();
	    String fullURL = url + ((urlParams != null) ? "?" + urlParams : "");
	    fullURL = ServletUtil.urlEncode(fullURL);
	    mav.addAttribute("fullURL", fullURL);
	    
		String lastStr = (String) session.getAttribute("lastAccessed");
		int lastAccessed = (lastStr == null) ? -1:Integer.parseInt(lastStr);
	    mav.addAttribute("lastAccessed", lastAccessed);
	    
		return "tier/feederTier";
    }
	
	private List<ViewableSubBus> createViewableSubBus(List<SubBus> subBusList) {
		List<ViewableSubBus> viewableList = new ArrayList<ViewableSubBus>(subBusList.size());
		
		for(SubBus subBus: subBusList) {
			ViewableSubBus viewable = new ViewableSubBus();
			viewable.setSubBus(subBus);
			
			if(subBus.getCurrentVarLoadPointID() != 0) {
				LitePoint point = pointDao.getLitePoint(subBus.getCurrentVarLoadPointID());
				viewable.setVarPoint(point);
			}
			if(subBus.getCurrentVoltLoadPointID() != 0) {
				LitePoint point = pointDao.getLitePoint(subBus.getCurrentVoltLoadPointID());
				viewable.setVoltPoint(point);
			}
			if(subBus.getCurrentWattLoadPointID() != 0) {
				LitePoint point = pointDao.getLitePoint(subBus.getCurrentWattLoadPointID());
				viewable.setWattPoint(point);
			}

			viewableList.add(viewable);
		}
		
		return viewableList;
	}
	
	private List<ViewableFeeder> createViewableFeeder(List<Feeder> feeders, CapControlCache cache) {
	    List<ViewableFeeder> viewableList = new ArrayList<ViewableFeeder>(feeders.size());
		
	    for (Feeder feeder: feeders) {
	    	String subBusName = cache.getSubBusNameForFeeder(feeder);
	    	ViewableFeeder viewable = new ViewableFeeder();
	    	
	    	viewable.setFeeder(feeder);
	    	viewable.setSubBusName(subBusName);
	    	
	    	viewableList.add(viewable);
	    }
	    
	    return viewableList;
	}
	
	private List<ViewableCapBank> createViewableCapBank(List<CapBankDevice> capBanks) {
	    List<ViewableCapBank> viewableList = new ArrayList<ViewableCapBank>(capBanks.size());
		
	    for (CapBankDevice cbc: capBanks) {
	    	LiteYukonPAObject controller = paoDao.getLiteYukonPAO(cbc.getControlDeviceID());
	    	ViewableCapBank viewable = new ViewableCapBank();
	    	
	    	viewable.setCapBankDevice(cbc);
	    	viewable.setControlDevice(controller);
	    	viewable.setTwoWayCbc(CBCUtils.isTwoWay(controller));
	    	viewable.setDevice701x(CBCUtils.is701xDevice(controller));
	    	
	    	viewableList.add(viewable);
	    }
	    
	    return viewableList;
	}
	
	private List<ViewableArea> createViewableArea(List<? extends StreamableCapObject> areas, CapControlCache cache, boolean isSpecialArea) {
	    List<ViewableArea> viewableList = new ArrayList<ViewableArea>(areas.size());
		
	    for (StreamableCapObject area: areas) {	    	
	    	ViewableArea viewable = new ViewableArea();
	    	List<ViewableSubStation> viewableSubStations = null;
	    	
	    	List<SubStation> subStations = null;
	    	if(isSpecialArea) {
	    		subStations = cache.getSubstationsBySpecialArea(area.getCcId());
	    	} else {
	    		subStations = cache.getSubstationsByArea(area.getCcId());
	    	}
	     	viewableSubStations = createViewableSubStation(subStations, cache);

	    	viewable.setArea(area);
	    	viewable.setSubStations(viewableSubStations);
	    	viewableList.add(viewable);
	    }
	    
	    return viewableList;
	}
	
	private List<ViewableSubStation> createViewableSubStation(List<SubStation> subStations, CapControlCache cache) {
		List<ViewableSubStation> viewableList = new ArrayList<ViewableSubStation>(subStations.size());
		
		for(SubStation subStation : subStations) {
    		ViewableSubStation viewable = new ViewableSubStation();
    		List<CapBankDevice> capBanks = cache.getCapBanksBySubStation(subStation);
    		List<Feeder> feeders = cache.getFeedersBySubStation(subStation);
    		
    		viewable.setSubStationName(subStation.getCcName());
    		viewable.setFeederCount(feeders.size());
    		viewable.setCapBankCount(capBanks.size());
    		
    		viewableList.add(viewable);
    	}
		
		return viewableList;
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
	public void setAuthDao (AuthDao authDao) {
		this.authDao = authDao;
	}
	
	@Autowired
	public void setPointDao (PointDao pointDao) {
		this.pointDao = pointDao;
	}
	
	@Autowired
	public void setPaoDao (PaoDao paoDao) {
		this.paoDao = paoDao;
	}
}

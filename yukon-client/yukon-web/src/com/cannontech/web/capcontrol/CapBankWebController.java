package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.LiteCapBankAdditional;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.web.CCSessionInfo;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

@Controller
@RequestMapping("/capbank/capBankLocations")
public class CapBankWebController {
	
	private FilterCacheFactory filterCacheFactory;
	private CapControlDao capControlDao;
	
	@RequestMapping
	public String capBankLocations(Integer value, LiteYukonUser user, ModelMap mav, Boolean specialArea) {
		
		if(user != null && value != null)
		{
			//Page data
			CapControlType type = capControlDao.getCapControlType(value);
			CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
		
			List<CapBankDevice> deviceList = cache.getCapBanksByTypeAndId(type, value);
			
			List<Integer> bankIds = new ArrayList<Integer>();
			for(CapBankDevice bank : deviceList) {
				bankIds.add(bank.getCcId());
			}
			List<LiteCapBankAdditional> additionalList = capControlDao.getCapBankAdditional(bankIds);

			mav.addAttribute("capBankList", deviceList);
			mav.addAttribute("addList",additionalList);
			
			//Bread Crumb Work
			if (specialArea) { 
				mav.addAttribute("baseTitle", "Special Substation Areas");
				mav.addAttribute("baseAddress","/capcontrol/subareas.jsp");
			} else {
				mav.addAttribute("baseTitle", "Substation Areas");
				mav.addAttribute("baseAddress","/capcontrol/subareas.jsp");
			}
	
			String areaTitle = "";
			String areaAddress = "/capcontrol/substations.jsp";
			String stationTitle = "";
			String stationAddress = "/capcontrol/feeders.jsp";
			String assetTitle = "";
			
			if (type == CapControlType.SUBBUS) {
				SubBus bus = cache.getSubBus(value);
				SubStation station = cache.getSubstation(bus.getParentID());
				StreamableCapObject area = cache.getArea(station.getParentID());
				
				areaTitle = area.getCcName();
				stationTitle = station.getCcName();
				
				areaAddress += "?" + CCSessionInfo.STR_CC_AREAID + "=" + area.getCcId();
				stationAddress += "?" + CCSessionInfo.STR_SUBID + "=" + station.getCcId() 
				               + "&specialArea=" + specialArea;
				assetTitle = bus.getCcName();
			} else if (type == CapControlType.FEEDER) {
				Feeder feeder = cache.getFeeder(value);
				SubBus bus = cache.getSubBus(feeder.getParentID());
				SubStation station = cache.getSubstation(bus.getParentID());
				StreamableCapObject area = cache.getArea(station.getParentID());
				
				areaTitle = area.getCcName();
				stationTitle = station.getCcName();
				
				areaAddress += "?" + CCSessionInfo.STR_CC_AREAID + "=" + area.getCcId();
				stationAddress += "?" + CCSessionInfo.STR_SUBID + "=" + station.getCcId() 
				               + "&specialArea=" + specialArea;
				assetTitle = feeder.getCcName();
			} else if (type == CapControlType.SUBSTATION) {
				SubStation station = cache.getSubstation(value);
				StreamableCapObject area = cache.getArea(station.getParentID());
				
				areaTitle = area.getCcName();
				stationTitle = station.getCcName();
				
				areaAddress += "?" + CCSessionInfo.STR_CC_AREAID + "=" + area.getCcId();
				stationAddress += "?" + CCSessionInfo.STR_SUBID + "=" + station.getCcId() 
				               + "&specialArea=" + specialArea;
				
				assetTitle = station.getCcName();
			}
			

			
			mav.addAttribute("areaAddress",areaAddress);
			mav.addAttribute("areaTitle",areaTitle);
			mav.addAttribute("stationAddress",stationAddress);
			mav.addAttribute("stationTitle",stationTitle);
			mav.addAttribute("assetTitle",assetTitle);
		}
		
		return "capBankLocations";
	}
	
	@Autowired
	public void setFilteredCapControlcache (FilterCacheFactory factory) {
		this.filterCacheFactory = factory;
	}
	
	@Autowired
	public void setCapControlDao(CapControlDao dao) {
		this.capControlDao = dao;
	}
}

package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.LiteCapBankAdditional;
import com.cannontech.capcontrol.BankLocation;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.clientutils.YukonLogManager;
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
	
    private Logger log = YukonLogManager.getLogger(CapBankWebController.class);
    
	private FilterCacheFactory filterCacheFactory;
	private CapControlDao capControlDao;
	
	@RequestMapping
	public String capBankLocations(Integer value, Boolean specialArea, LiteYukonUser user, ModelMap mav ) {
		
		//Page data
		CapControlType type = capControlDao.getCapControlType(value);
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
	
		List<CapBankDevice> deviceList = cache.getCapBanksByTypeAndId(type, value);
		
		List<Integer> bankIds = new ArrayList<Integer>();
		for(CapBankDevice bank : deviceList) {
			bankIds.add(bank.getCcId());
		}
		List<LiteCapBankAdditional> additionalList = capControlDao.getCapBankAdditional(bankIds);

		//Build a map to keep this from being O^2
		Map<Integer,LiteCapBankAdditional> mapBankAdditionals = new HashMap<Integer,LiteCapBankAdditional>(); 
		for (LiteCapBankAdditional capAdd : additionalList) {
		    mapBankAdditionals.put(capAdd.getDeviceId(),capAdd);
		}

		//Form a single list so there cannot be any ordering issues.
		List<BankLocation> bankLocationList = new ArrayList<BankLocation>();
		for (CapBankDevice capBank : deviceList) {
		    LiteCapBankAdditional additionInfo = mapBankAdditionals.get(capBank.getCcId());
		    
		    if (additionInfo != null) {
		        BankLocation location = new BankLocation(capBank.getCcName(),
		                                                 additionInfo.getSerialNumber(),
		                                                 capBank.getCcArea(),
		                                                 additionInfo.getDrivingDirections());
		        bankLocationList.add(location);
		    } else {
		        log.warn("Cap Bank Addition info missing for bank id: " + capBank.getCcId());
		    }
        }
		
		mav.addAttribute("capBankList", bankLocationList);
		
		String specialAreaParameters = "";
		
		//Bread Crumb Work
		if (specialArea) { 
			mav.addAttribute("baseTitle", "Special Substation Areas");
			specialAreaParameters = "?isSpecialArea=true";
		} else {
			mav.addAttribute("baseTitle", "Substation Areas");
		}
		
		mav.addAttribute("baseAddress","/spring/capcontrol/tier/areas" + specialAreaParameters);
		
		String areaTitle = "";
		String areaAddress = "/spring/capcontrol/tier/substations";
		String stationTitle = "";
		String stationAddress = "/spring/capcontrol/tier/feeders";
		String assetTitle = "";
		
		StreamableCapObject area;
		SubStation station;

		if (type == CapControlType.SUBBUS) {
			SubBus bus = cache.getSubBus(value);
			station = cache.getSubstation(bus.getParentID());
			
			assetTitle = bus.getCcName();
		} else if (type == CapControlType.FEEDER) {
			Feeder feeder = cache.getFeeder(value);
			SubBus bus = cache.getSubBus(feeder.getParentID());			
			station = cache.getSubstation(bus.getParentID());
			
			assetTitle = feeder.getCcName();
		} else { //Station
			//If this is not a station, a not found exception will be thrown from cache
			station = cache.getSubstation(value);
			assetTitle = station.getCcName();
		}
		
		if(specialArea) {
			area = cache.getCBCSpecialArea(station.getSpecialAreaId());
		} else {
			area = cache.getCBCArea(station.getParentID());
		}
		
		areaTitle = area.getCcName();
		stationTitle = station.getCcName();

		areaAddress += "?areaId=" + area.getCcId() + "&isSpecialArea=" + specialArea;;
		stationAddress += "?subStationId=" + station.getCcId() + "&areaId=" + area.getCcId() 
		               + "&isSpecialArea=" + specialArea;
		
		mav.addAttribute("areaAddress",areaAddress);
		mav.addAttribute("areaTitle",areaTitle);
		mav.addAttribute("stationAddress",stationAddress);
		mav.addAttribute("stationTitle",stationTitle);
		mav.addAttribute("assetTitle",assetTitle);
		
		return "capBankLocations.jsp";
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

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

import com.cannontech.capcontrol.BankLocation;
import com.cannontech.capcontrol.LiteCapBankAdditional;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.web.updater.point.PointUpdateBackingService;

@Controller
@RequestMapping("/capbank/*")
public class CapBankWebController {
	
    private Logger log = YukonLogManager.getLogger(CapBankWebController.class);
    
	private FilterCacheFactory filterCacheFactory;
	private CapControlDao capControlDao;
	private CachingPointFormattingService cachingPointFormattingService;
	private PointUpdateBackingService pointUpdateBackingService;
	
	@RequestMapping
	public String capBankLocations(ModelMap model, LiteYukonUser user, int value, boolean specialArea) {
		
		//Page data
		CapControlType type = capControlDao.getCapControlType(value);
		CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
	
		List<CapBankDevice> deviceList = cache.getCapBanksByTypeAndId(type, value);
		
		List<Integer> bankIds = new ArrayList<Integer>();
		for (CapBankDevice bank : deviceList) {
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
		        log.warn("Cap Bank Additional info missing for bank id: " + capBank.getCcId());
		    }
        }
		
		SubStation substation;
		StreamableCapObject area;
		if (type == CapControlType.SUBBUS) {
			SubBus bus = cache.getSubBus(value);
			substation = cache.getSubstation(bus.getParentID());
			area = getArea(specialArea, cache, substation);
		} else if (type == CapControlType.FEEDER) {
			Feeder feeder = cache.getFeeder(value);
			SubBus bus = cache.getSubBus(feeder.getParentID());			
			substation = cache.getSubstation(bus.getParentID());
			area = getArea(specialArea, cache, substation);
		} else { //Station
			//If this is not a station, a not found exception will be thrown from cache
			substation = cache.getSubstation(value);
			area = getArea(specialArea, cache, substation);
		}
		
		model.addAttribute("capBankList", bankLocationList);
		model.addAttribute("areaId", area.getCcId());
		model.addAttribute("areaName", area.getCcName());
		model.addAttribute("substationId", substation.getCcId());
		model.addAttribute("substationName", substation.getCcName());
		model.addAttribute("isSpecialArea", specialArea);
		
		return "capBankLocations.jsp";
	}
	
	@RequestMapping
	public String cbcPoints(ModelMap model, int cbcId) {
        Map<String, List<LitePoint>> pointTimestamps = capControlDao.getSortedCBCPointTimeStamps(cbcId);
        model.addAttribute("pointMap", pointTimestamps);
        
        List<LitePoint> pointList = new ArrayList<LitePoint>();
        for(List<LitePoint> list : pointTimestamps.values()) {
            pointList.addAll(list);
        }
        
        // Get some pre work done to speed things up on the page load.
        cachingPointFormattingService.addLitePointsToCache(pointList);
        pointUpdateBackingService.notifyOfImminentPoints(pointList);
        
        return "cbcPointTimestamps.jsp";
	}

    private StreamableCapObject getArea(Boolean specialArea, CapControlCache cache, SubStation substation) {
        if (specialArea) {
            return cache.getCBCSpecialArea(substation.getSpecialAreaId());
        } else {
            return cache.getCBCArea(substation.getParentID());
        }
    }
	
	@Autowired
	public void setFilteredCapControlcache (FilterCacheFactory factory) {
		this.filterCacheFactory = factory;
	}
	
	@Autowired
	public void setCapControlDao(CapControlDao dao) {
		this.capControlDao = dao;
	}
	
	@Autowired
	public void setCachingPointFormattingService(CachingPointFormattingService cachingPointFormattingService) {
        this.cachingPointFormattingService = cachingPointFormattingService;
    }
	
	@Autowired
	public void setPointUpdateBackingService(PointUpdateBackingService pointUpdateBackingService) {
        this.pointUpdateBackingService = pointUpdateBackingService;
    }
	
}
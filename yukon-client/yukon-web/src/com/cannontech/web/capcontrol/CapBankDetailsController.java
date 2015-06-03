package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.BankLocation;
import com.cannontech.capcontrol.CBCPointGroup;
import com.cannontech.capcontrol.LiteCapBankAdditional;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.google.common.collect.ImmutableMap;

@Controller
@RequestMapping("/capbank/*")
public class CapBankDetailsController {
    
    private Logger log = YukonLogManager.getLogger(CapBankDetailsController.class);
    
    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private CapControlDao capControlDao;
    @Autowired private CachingPointFormattingService cachingPointFormattingService;
    @Autowired private PaoDao paoDao;
    @Autowired private AttributeService attributeService;

    private static final Map<BuiltInAttribute,String> formatMappings = ImmutableMap.<BuiltInAttribute,String>builder()
        .put(BuiltInAttribute.FIRMWARE_VERSION, "{rawValue|com.cannontech.cbc.util.CapControlUtils.convertToFirmwareVersion}")
        .put(BuiltInAttribute.IP_ADDRESS, "{rawValue|com.cannontech.cbc.util.CapControlUtils.convertToOctalIp}")
        .put(BuiltInAttribute.NEUTRAL_CURRENT_SENSOR, "{rawValue|com.cannontech.cbc.util.CapControlUtils.convertNeutralCurrent}")
        .put(BuiltInAttribute.SERIAL_NUMBER, "{rawValue|com.cannontech.cbc.util.CapControlUtils.convertLong}")
        .put(BuiltInAttribute.UDP_PORT, "{rawValue|com.cannontech.cbc.util.CapControlUtils.convertLong}")
        .put(BuiltInAttribute.LAST_CONTROL_REASON, "{rawValue|com.cannontech.cbc.util.CapControlUtils.convertControlReason}")
        .put(BuiltInAttribute.IGNORED_CONTROL_REASON, "{rawValue|com.cannontech.cbc.util.CapControlUtils.convertControlReason}")
        .build();

    @RequestMapping("capBankLocations")
    public String capBankLocations(ModelMap model, LiteYukonUser user, int value) {
        
        //Page data
        CapControlType type = capControlDao.getCapControlType(value);
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
    
        List<CapBankDevice> deviceList = cache.getCapBanksByTypeAndId(type, value);
        
        List<Integer> bankIds = new ArrayList<Integer>();
        for (CapBankDevice bank : deviceList) {
            bankIds.add(bank.getCcId());
        }
        List<LiteCapBankAdditional> additionalList = capControlDao.getCapBankAdditional(bankIds);

        //Build a map to keep this from being O(n^2)
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
            area = cache.getArea(substation.getParentID());
        } else if (type == CapControlType.FEEDER) {
            Feeder feeder = cache.getFeeder(value);
            SubBus bus = cache.getSubBus(feeder.getParentID());
            substation = cache.getSubstation(bus.getParentID());
            area = cache.getArea(substation.getParentID());
        } else { //Station
            //If this is not a station, a not found exception will be thrown from cache
            substation = cache.getSubstation(value);
            area = cache.getArea(substation.getParentID());
        }
        
        model.addAttribute("capBankList", bankLocationList);
        model.addAttribute("bc_areaId", area.getCcId());
        model.addAttribute("bc_areaName", area.getCcName());
        model.addAttribute("substationId", substation.getCcId());
        model.addAttribute("substationName", substation.getCcName());
        
        return "capBankLocations.jsp";
    }
    
    @RequestMapping("cbcPoints")
    public String cbcPoints(ModelMap model, int cbcId) {

        Map<CBCPointGroup, List<LitePoint>> pointTimestamps = capControlDao.getSortedCBCPointTimeStamps(cbcId);
        model.addAttribute("pointMap", pointTimestamps);

        PaoType paoType = paoDao.getLiteYukonPAO(cbcId).getPaoType();
        Map<LitePoint, String> formatForAnalogPoints = new LinkedHashMap<>();

        for (LitePoint point : pointTimestamps.get(CBCPointGroup.ANALOG)) {
            PointIdentifier pid = PointIdentifier.createPointIdentifier(point);
            PaoTypePointIdentifier pptId = PaoTypePointIdentifier.of(paoType, pid);

            String pointValueFormat = "SHORT";

            //This set should contain 0 items if there is not a special format, or 1 if there is
            Set<BuiltInAttribute> attributes = attributeService.findAttributesForPoint(pptId, formatMappings.keySet());
            for (BuiltInAttribute attribute: attributes) {
                pointValueFormat = formatMappings.get(attribute);
            }

            formatForAnalogPoints.put(point, pointValueFormat);
        }

        model.addAttribute("formatForAnalogPoints", formatForAnalogPoints);

        Map<String, CBCPointGroup> cbcPointGroup = new HashMap<>();
        for (CBCPointGroup pointGroup : CBCPointGroup.values()) {
            cbcPointGroup.put(pointGroup.name(), pointGroup);
        }

        model.put("cbcPointGroup", cbcPointGroup);
        
        List<LitePoint> pointList = new ArrayList<LitePoint>();
        for(List<LitePoint> list : pointTimestamps.values()) {
            pointList.addAll(list);
        }
        
        // Get some pre work done to speed things up on the page load.
        cachingPointFormattingService.addLitePointsToCache(pointList);
        
        return "cbcPointTimestamps.jsp";
    }

}
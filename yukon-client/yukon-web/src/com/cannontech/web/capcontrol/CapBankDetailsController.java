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
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.CachingPointFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.service.SubstationService;
import com.cannontech.yukon.IDatabaseCache;
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
    @Autowired private SubstationService substationService;
    @Autowired private SubstationDao substationDao;
    @Autowired private IDatabaseCache dbCache;

    private static final Map<BuiltInAttribute,String> formatMappings = ImmutableMap.<BuiltInAttribute,String>builder()
        .put(BuiltInAttribute.FIRMWARE_VERSION, "{rawValue|firmwareVersion}")
        .put(BuiltInAttribute.IP_ADDRESS, "{rawValue|ipAddress}")
        .put(BuiltInAttribute.NEUTRAL_CURRENT_SENSOR, "{rawValue|neutralCurrent}")
        .put(BuiltInAttribute.SERIAL_NUMBER, "{rawValue|long}")
        .put(BuiltInAttribute.UDP_PORT, "{rawValue|long}")
        .put(BuiltInAttribute.LAST_CONTROL_REASON, "{rawValue|lastControlReason}")
        .put(BuiltInAttribute.IGNORED_CONTROL_REASON, "{rawValue|ignoredControlReason}")
        .build();

    @RequestMapping("capBankLocations")
    public String capBankLocations(ModelMap model, LiteYukonUser user, int value) {
        boolean isSubstationOrphan = false;
        List<CapBankDevice> deviceList = null;
        List<ViewableCapBank> capBanks = null;
        Map<Integer, LiteYukonPAObject> allLitePaos = null;
        SubStation substation = null;

        // Page data
        CapControlType type = capControlDao.getCapControlType(value);
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

        List<Integer> bankIds = new ArrayList<Integer>();
        try {
            deviceList = cache.getCapBanksByTypeAndId(type, value);
            if (deviceList != null) {
                for (CapBankDevice bank : deviceList) {
                    bankIds.add(bank.getCcId());
                }
            }
        } catch (NotFoundException nfe) {
            if (type == CapControlType.SUBSTATION) {
                // Check whether the substation is orphan
                List<LiteCapControlObject> ccObjects = substationDao.getOrphans();
                for (LiteCapControlObject ccSubstation : ccObjects) {
                    if (ccSubstation.getId().intValue() == value) {
                        isSubstationOrphan = true;
                        substation = new SubStation();
                        substation.setCcId(ccSubstation.getId());
                        substation.setCcName(ccSubstation.getName());
                        List<ViewableSubBus> subBuses = substationService.getBusesForSubstation(value);
                        List<ViewableFeeder> feeders = substationService.getFeedersForSubBuses(subBuses);
                        capBanks = substationService.getCapBanksForFeeders(feeders);
                        break;
                    }
                }

                if (capBanks != null) {
                    model.addAttribute("orphan", isSubstationOrphan);
                    for (ViewableCapBank bank : capBanks) {
                        bankIds.add(bank.getCcId());
                    }
                } else {
                    throw nfe;
                }
            }
        }

        List<LiteCapBankAdditional> additionalList = capControlDao.getCapBankAdditional(bankIds);

        // Build a map to keep this from being O(n^2)
        Map<Integer, LiteCapBankAdditional> mapBankAdditionals = new HashMap<Integer, LiteCapBankAdditional>();
        for (LiteCapBankAdditional capAdd : additionalList) {
            mapBankAdditionals.put(capAdd.getDeviceId(), capAdd);
        }

        // Form a single list so there cannot be any ordering issues.
        List<BankLocation> bankLocationList = new ArrayList<BankLocation>();
        if (deviceList != null && !deviceList.isEmpty()) {
            for (CapBankDevice capBank : deviceList) {
                LiteCapBankAdditional additionInfo = mapBankAdditionals.get(capBank.getCcId());

                if (additionInfo != null) {
                    BankLocation location =
                        new BankLocation(capBank.getCcName(), additionInfo.getSerialNumber(), capBank.getCcArea(),
                            additionInfo.getDrivingDirections());
                    bankLocationList.add(location);
                } else {
                    log.warn("Cap Bank Additional info missing for bank id: " + capBank.getCcId());
                }
            }
        } else {
            allLitePaos = dbCache.getAllPaosMap();
            for (ViewableCapBank capBank : capBanks) {
                LiteCapBankAdditional additionInfo = mapBankAdditionals.get(capBank.getCcId());
                LiteYukonPAObject pao = allLitePaos.get(capBank.getCcId());

                if (additionInfo != null) {
                    BankLocation location =
                        new BankLocation(capBank.getCcName(), additionInfo.getSerialNumber(), pao.getPaoDescription(),
                            additionInfo.getDrivingDirections());
                    bankLocationList.add(location);
                } else {
                    log.warn("Cap Bank Additional info missing for bank id: " + capBank.getCcId());
                }
            }
        }
        StreamableCapObject area = null;
        if (type == CapControlType.SUBBUS) {
            SubBus bus = cache.getSubBus(value);
            substation = cache.getSubstation(bus.getParentID());
            area = cache.getArea(substation.getParentID());
        } else if (type == CapControlType.FEEDER) {
            Feeder feeder = cache.getFeeder(value);
            SubBus bus = cache.getSubBus(feeder.getParentID());
            substation = cache.getSubstation(bus.getParentID());
            area = cache.getArea(substation.getParentID());
        } else if (!isSubstationOrphan) {// Station
            // If this is not a station, a not found exception will be thrown from cache
            substation = cache.getSubstation(value);
            area = cache.getArea(substation.getParentID());
        }

        model.addAttribute("capBankList", bankLocationList);
        if (!isSubstationOrphan) {
            model.addAttribute("areaId", area.getCcId());
            model.addAttribute("areaName", area.getCcName());
        }
        model.addAttribute("substationId", substation.getCcId());
        model.addAttribute("substationName", substation.getCcName());

        return "capBankLocations.jsp";
    }
    
    @RequestMapping("cbcPoints")
    public String cbcPoints(ModelMap model, int cbcId) {

        Map<CBCPointGroup, List<LitePoint>> pointTimestamps = capControlDao.getSortedCBCPointTimeStamps(cbcId);
        model.addAttribute("pointMap", pointTimestamps);

        PaoType paoType = paoDao.getLiteYukonPAO(cbcId).getPaoType();

        List<LitePoint> analogPoints = pointTimestamps.get(CBCPointGroup.ANALOG);
        Map<LitePoint, String> formatForAnalogPoints = getFormatMappings(paoType, analogPoints);

        model.addAttribute("formatForAnalogPoints", formatForAnalogPoints);

        List<LitePoint> configurableParams = pointTimestamps.get(CBCPointGroup.CONFIGURABLE_PARAMETERS);
        Map<LitePoint, String> formatForConfigurablePoints = getFormatMappings(paoType, configurableParams);

        model.addAttribute("formatForConfigurablePoints", formatForConfigurablePoints);

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

    private Map<LitePoint, String> getFormatMappings(PaoType paoType, List<LitePoint> litePoints) {

        Map<LitePoint, String> formatForPoints = new LinkedHashMap<>();

        for (LitePoint point : litePoints) {
            PointIdentifier pid = PointIdentifier.createPointIdentifier(point);
            PaoTypePointIdentifier pptId = PaoTypePointIdentifier.of(paoType, pid);

            String pointValueFormat = "SHORT";

            //This set should contain 0 items if there is not a special format, or 1 if there is
            Set<BuiltInAttribute> attributes = attributeService.findAttributesForPoint(pptId, formatMappings.keySet());
            for (BuiltInAttribute attribute: attributes) {
                if (formatMappings.get(attribute) != null) {
                    pointValueFormat = formatMappings.get(attribute);
                }
            }

            formatForPoints.put(point, pointValueFormat);
        }
        return formatForPoints;
    }

}
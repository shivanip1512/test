package com.cannontech.web.tools.mapping.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.model.DisplayableMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.message.metadata.RfnMetadata;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceMetadataService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.tools.mapping.model.Filter;
import com.cannontech.web.tools.mapping.model.Group;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
public class MapController {
    
    private static final Logger log = YukonLogManager.getLogger(MapController.class);
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private PointService pointService;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RfnGatewayDataCache gatewayDataCache;
    @Autowired private RfnDeviceMetadataService metadataService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
    List<BuiltInAttribute> attributes = ImmutableList.of(
        BuiltInAttribute.VOLTAGE,
        BuiltInAttribute.VOLTAGE_PHASE_A,
        BuiltInAttribute.VOLTAGE_PHASE_B,
        BuiltInAttribute.VOLTAGE_PHASE_C,
        BuiltInAttribute.USAGE);

    /**
     * Meant for device collections that are not static. Like collections based on 
     * the violations device group of a status point or outage monitor.
     */
    @RequestMapping("/map/dynamic")
    public String dynamcic(ModelMap model, DeviceCollection deviceCollection) {
        
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("description", deviceCollection.getDescription());
        model.addAttribute("dynamic", true);
        
        return "map/map.jsp";
    }

    @RequestMapping(value="/map")
    public String map(ModelMap model, DeviceCollection deviceCollection, YukonUserContext userContext) {
        
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("description", deviceCollection.getDescription());
        
        Map<AttributeGroup, Set<BuiltInAttribute>> groups = BuiltInAttribute.getAllGroupedStatusTypeAttributes();
        model.addAttribute("attributes", objectFormattingService.sortDisplayableValues(groups, userContext));
        
        Filter filter = new Filter();
        model.addAttribute("filter", filter);
        
        return "map/map.jsp";
    }
    
    @RequestMapping("/map/device/{id}/info")
    public String info(ModelMap model, @PathVariable int id) {
        
        YukonPao pao = databaseCache.getAllPaosMap().get(id);
        PaoType type = pao.getPaoIdentifier().getPaoType();
        DisplayablePao displayable = paoLoadingService.getDisplayablePao(pao);
        if (displayable instanceof DisplayableMeter) {
            DisplayableMeter meter = (DisplayableMeter) displayable;
            if (StringUtils.isNotBlank(meter.getMeter().getRoute())) {
                model.addAttribute("showRoute", true);
            }
            if (StringUtils.isNotBlank(meter.getMeter().getSerialOrAddress())) {
                model.addAttribute("showAddressOrSerial", true);
            }
            if (StringUtils.isNotBlank(meter.getMeter().getMeterNumber())) {
                model.addAttribute("showMeterNumber", true);
            }
        }
        if (type.isRfGateway()) {
            try {
                RfnGatewayData gateway = gatewayDataCache.get(pao.getPaoIdentifier());
                model.addAttribute("gatewayIPAddress", gateway.getIpAddress());
            } catch (NmCommunicationException e) {
                log.error("Failed to get gateway data for " + id, e);           
            }
        }
        if (type.isRfn()) {
            try {
                RfnDevice rfnDevice = rfnDeviceDao.getDeviceForId(id);
                Map<RfnMetadata, Object> metadata = metadataService.getMetadata(rfnDevice);
                model.addAttribute("macAddress", metadataService.getMetaDataValueAsString(RfnMetadata.NODE_ADDRESS, metadata));
                model.addAttribute("primaryGateway", metadataService.getMetaDataValueAsString(RfnMetadata.PRIMARY_GATEWAY, metadata));
            } catch (NmCommunicationException e) {
                log.error("Failed to get metadata for " + id, e);           
            } catch (NotFoundException e) {
                log.error("Failed to find RFN Device for " + id, e);           
            }
        }

        model.addAttribute("pao", displayable);
        
        List<Attribute> supported = new ArrayList<>();

        for(Attribute attribute : attributes) {
            if (attributeService.isAttributeSupported(pao, attribute)) {
                supported.add(attribute);
            }
        }
        model.addAttribute("attributes", supported);
        
        return "map/info.jsp";
    }
    
    @RequestMapping(value="/map/device/{id}", method=RequestMethod.DELETE)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public @ResponseBody Map<String, String> deleteCoordinates(@PathVariable int id, YukonUserContext userContext, 
                                                    HttpServletResponse response) {
        
        Map<String, String> json = new HashMap<>();
        try {
            paoLocationService.deleteLocationForPaoId(id);
            log.debug("Deleted coordinates for paoId " + id);
            response.setStatus(HttpStatus.OK.value());
            return json;
        } catch (Exception e) {
            log.error("Error deleting coordinates for paoId " + id);
            
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String message = accessor.getMessage("yukon.web.modules.tools.map.deleteCoordinates.error", e.getMessage());
            json.put("message", message);
            
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return json;
        }
    }
    
    @RequestMapping("/map/locations")
    public @ResponseBody FeatureCollection locations(DeviceCollection deviceCollection) {
        
        FeatureCollection locations = paoLocationService.getLocationsAsGeoJson(deviceCollection.getDeviceList());
        
        return locations;
    }
    
    @RequestMapping("/map/filter/state-groups")
    public @ResponseBody List<LiteStateGroup> states(DeviceCollection deviceCollection, BuiltInAttribute attribute) {
        
        List<LiteStateGroup> stateGroups = attributeService.findStateGroups(deviceCollection.getDeviceList(), attribute);
        
        return stateGroups;
    }
    
    @RequestMapping("/map/filter")
    public @ResponseBody Map<Integer, Boolean> filter(DeviceCollection deviceCollection, @ModelAttribute Filter filter) {
        
        Map<Integer, Boolean> results = new HashMap<>();
        Map<Integer, Group> groups = Maps.uniqueIndex(filter.getGroups(), Group.ID_FUNCTION);
        
        Set<SimpleDevice> devices = Sets.newHashSet(deviceCollection.getDeviceList());
        BiMap<LitePoint, PaoIdentifier> points = 
                attributeService.getPoints(devices, filter.getAttribute()).inverse();
        
        Map<Integer, LitePoint> statusPoints = new HashMap<>();
        Map<Integer, LitePoint> nonStatusPoints = new HashMap<>();
        
        for (LitePoint point : points.keySet()) {
            if (point.getPointTypeEnum().isStatus()) {
                statusPoints.put(point.getLiteID(), point);
            } else {
                nonStatusPoints.put(point.getLiteID(), point);
            }
        }
        
        // Determine visibility for status points
        Set<? extends PointValueQualityHolder> statusValues = asyncDynamicDataSource.getPointValues(statusPoints.keySet());
        
        for (PointValueQualityHolder pvqh : statusValues) {
            LitePoint lp = statusPoints.get(pvqh.getId());
            Group group = groups.get(lp.getStateGroupID());
            LiteState state = stateGroupDao.findLiteState(lp.getStateGroupID(), (int) pvqh.getValue());
            if (state.getStateRawState() == group.getState()) {
                results.put(points.get(lp).getPaoId(), true);
            } else {
                results.put(points.get(lp).getPaoId(), false);
            }
        }
        
        // Determine visibility for NON status points
        Map<Integer, Set<Signal>> signals = asyncDynamicDataSource.getSignals(nonStatusPoints.keySet());
        
        for (Integer pointId : signals.keySet()) {
            LitePoint lp = nonStatusPoints.get(pointId);
            Group group = groups.get(lp.getStateGroupID());
            LiteState state = pointService.getCurrentStateForNonStatusPoint(lp, signals.get(pointId));
            if (state.getStateRawState() == group.getState()) {
                results.put(points.get(lp).getPaoId(), true);
            } else {
                results.put(points.get(lp).getPaoId(), false);
            }
        }
        
        // Throw unsupported devices in as hidden
        Set<SimpleDevice> unsupported = Sets.difference(devices, Sets.newHashSet(PaoUtils.asSimpleDeviceList(points.values())));
        for (SimpleDevice device : unsupported) {
            results.put(device.getDeviceId(), false);
        }
        
        return results;
    }
}

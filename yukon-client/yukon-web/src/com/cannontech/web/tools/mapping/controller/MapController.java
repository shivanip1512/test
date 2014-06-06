package com.cannontech.web.tools.mapping.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointService;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.mapping.model.Filter;
import com.cannontech.web.tools.mapping.model.Group;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
public class MapController {
    
    @Autowired private AttributeService attributeService;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private PointService pointService;
    @Autowired private StateDao stateDao;
    
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
        
        Map<AttributeGroup, Set<BuiltInAttribute>> groups = BuiltInAttribute.getAllGroupedAttributes();
        model.addAttribute("attributes", objectFormattingService.sortDisplayableValues(groups, userContext));
        
        Filter filter = new Filter();
        model.addAttribute("filter", filter);
        
        return "map/map.jsp";
    }
    
    @RequestMapping("/map/device/{id}/info")
    public String info(ModelMap model, @PathVariable int id) {
        
        YukonPao pao = paoDao.getYukonPao(id);
        DisplayablePao displayable = paoLoadingService.getDisplayablePao(pao);
        PaoLocation location = paoLocationDao.getLocation(id);
        
        model.addAttribute("pao", displayable);
        model.addAttribute("location", location);
        
        List<Attribute> attributes = new ArrayList<>();
        if (attributeService.isAttributeSupported(pao, BuiltInAttribute.VOLTAGE)) {
            attributes.add(BuiltInAttribute.VOLTAGE);
        }
        if (attributeService.isAttributeSupported(pao, BuiltInAttribute.VOLTAGE_PHASE_A)) {
            attributes.add(BuiltInAttribute.VOLTAGE_PHASE_A);
        }
        if (attributeService.isAttributeSupported(pao, BuiltInAttribute.VOLTAGE_PHASE_B)) {
            attributes.add(BuiltInAttribute.VOLTAGE_PHASE_B);
        }
        if (attributeService.isAttributeSupported(pao, BuiltInAttribute.VOLTAGE_PHASE_C)) {
            attributes.add(BuiltInAttribute.VOLTAGE_PHASE_C);
        }
        if (attributeService.isAttributeSupported(pao, BuiltInAttribute.USAGE)) {
            attributes.add(BuiltInAttribute.USAGE);
        }
        model.addAttribute("attributes", attributes);
        
        return "map/info.jsp";
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
        BiMap<LitePoint, SimpleDevice> points = 
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
        Set<? extends PointValueQualityHolder> statusValues = dynamicDataSource.getPointValues(statusPoints.keySet());
        
        for (PointValueQualityHolder pvqh : statusValues) {
            LitePoint lp = statusPoints.get(pvqh.getId());
            Group group = groups.get(lp.getStateGroupID());
            LiteState state = stateDao.findLiteState(lp.getStateGroupID(), (int) pvqh.getValue());
            if (state.getStateRawState() == group.getState()) {
                results.put(points.get(lp).getDeviceId(), true);
            } else {
                results.put(points.get(lp).getDeviceId(), false);
            }
        }
        
        // Determine visibility for NON status points
        Map<Integer, Set<Signal>> signals = dynamicDataSource.getSignals(nonStatusPoints.keySet());
        
        for (Integer pointId : signals.keySet()) {
            LitePoint lp = nonStatusPoints.get(pointId);
            Group group = groups.get(lp.getStateGroupID());
            LiteState state = pointService.getCurrentStateForNonStatusPoint(lp, signals.get(pointId));
            if (state.getStateRawState() == group.getState()) {
                results.put(points.get(lp).getDeviceId(), true);
            } else {
                results.put(points.get(lp).getDeviceId(), false);
            }
        }
        
        // Throw unsupported devices in as hidden
        Set<SimpleDevice> unsupported = Sets.difference(devices, points.values());
        for (SimpleDevice device : unsupported) {
            results.put(device.getDeviceId(), false);
        }
        
        return results;
    }
}

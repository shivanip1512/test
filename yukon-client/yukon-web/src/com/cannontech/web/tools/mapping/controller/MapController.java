package com.cannontech.web.tools.mapping.controller;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Point;
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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.mapping.model.Filter;
import com.cannontech.web.tools.mapping.model.Group;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.cannontech.web.tools.mapping.service.PaoLocationService.FeaturePropertyType;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
public class MapController {
    
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private PaoDao paoDao;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private AttributeService attributeService;
    
    @RequestMapping("/map")
    public String map(ModelMap model) {
        //TODO
        return "map/map.jsp";
    }

    @RequestMapping(value="/map", params="collectionType")
    public String map(ModelMap model, DeviceCollection deviceCollection, YukonUserContext userContext) {
        
        model.addAttribute("deviceCollection", deviceCollection);
        ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> groups = BuiltInAttribute.getAllGroupedAttributes();
        model.addAttribute("attributes", objectFormattingService.sortDisplayableValues(groups, userContext));
        
        Filter filter = new Filter();
        model.addAttribute("filter", filter);
        
        return "map/map.jsp";
    }
    
    @RequestMapping("/map/device/{id}/info")
    public String info(ModelMap model, @PathVariable int id) {
        
        YukonPao pao = paoDao.getYukonPao(id);
        DisplayablePao displayable = paoLoadingService.getDisplayablePao(pao);
//        PaoLocation location = paoLocationDao.getLocation(id);
        
        PaoLocation location = new PaoLocation();
        location.setLatitude(45.254861);
        location.setLongitude(-93.557715);
        location.setPaoIdentifier(pao.getPaoIdentifier());
        
        model.addAttribute("pao", displayable);
        model.addAttribute("location", location);
        
        return "map/info.jsp";
    }
    
    @RequestMapping("/map/locations")
    public @ResponseBody FeatureCollection locations(DeviceCollection deviceCollection) {
        
//        FeatureCollection locations = paoLocationService.getLastLocationsAsGeoJson(collection.getDeviceList());
        FeatureCollection locations = getFakeLocations(deviceCollection.getDeviceList());
        
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
        Map<Integer, LitePoint> pointIdToPoint = new HashMap<>();
        for (LitePoint point : points.keySet()) {
            pointIdToPoint.put(point.getLiteID(), point);
        }
        
        Collection<Integer> pointIds = Collections2.transform(points.keySet(), LitePoint.ID_FUNCTION);
        Set<? extends PointValueQualityHolder> values = dynamicDataSource.getPointValue(Sets.newHashSet(pointIds));
        
        for (PointValueQualityHolder pvqh : values) {
            LitePoint lp = pointIdToPoint.get(pvqh.getId());
            Group group = groups.get(lp.getStateGroupID());
            if ((int)pvqh.getValue() == group.getState()) {
                results.put(points.get(lp).getDeviceId(), true);
            } else {
                results.put(points.get(lp).getDeviceId(), false);
            }
        }
        
        Set<SimpleDevice> unsupported = Sets.difference(devices, points.values());
        for (SimpleDevice device : unsupported) {
            results.put(device.getDeviceId(), false);
        }
        
        return results;
    }
        
    private FeatureCollection getFakeLocations(List<SimpleDevice> devices) {
        
        FeatureCollection collection = new FeatureCollection();
        
        Map<String, Object> crsProperties = new HashMap<>();
        crsProperties.put("name", "EPSG:4326"); // Yukon is assuming WSG 84 (EPSG:4326)
        Crs crs = new Crs();
        crs.setProperties(crsProperties);
        
        collection.setCrs(crs);
        
        DecimalFormat df = new DecimalFormat("##.######");
        
        for (SimpleDevice device : devices) {
            
            Feature feature = new Feature();
            feature.setId(Integer.toString(device.getDeviceId()));
            
            double latitude = Double.parseDouble(df.format((Math.random() * (45.3 - 45.0)) + 45.0));
            double longitude = Double.parseDouble(df.format(((Math.random() * (93.5 - 93.0)) + 93.0) * -1));
            if (collection.getFeatures().size() == 0) {
                latitude = 45.254861;
                longitude = -93.557715;
            }
            
            Point point = new Point(longitude, latitude);
            feature.setGeometry(point);
            
            feature.getProperties().put(FeaturePropertyType.PAO_IDENTIFIER.getKeyName(), device.getPaoIdentifier());
            
            collection.add(feature);
        }
        
        return collection;
    }
    
    @ModelAttribute("iconMap")
    private Map<PaoType, String> getPaoTypeIconMap() {
        
        Map<PaoType, String> icons = new HashMap<>();
        for (PaoType type : PaoType.getMeterTypes()) {
            if (type.isWaterMeter()) {
                icons.put(type, "marker-water.png");
            } else {
                icons.put(type, "marker-electric.png");
            }
        }
        
        return icons;
    }
    
}
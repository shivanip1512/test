package com.cannontech.web.tools.mapping.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.cannontech.web.tools.mapping.service.PaoLocationService.FeaturePropertyType;
import com.cannontech.web.tools.mapping.model.Filter;
import com.cannontech.web.tools.mapping.model.Group;
import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
public class MapController {
    
    @Autowired private DynamicDataSource dds;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private ObjectFormattingService objectFormatting;
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
        model.addAttribute("attributes", objectFormatting.sortDisplayableValues(groups, userContext));
        
        Filter filter = new Filter();
        model.addAttribute("filter", filter);
        
        return "map/map.jsp";
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
    public @ResponseBody List<Integer> filter(DeviceCollection deviceCollection, @ModelAttribute("filter") final Filter filter) {
        
        List<Integer> paoIds = new ArrayList<>();
        Map<Integer, Group> groups = Maps.uniqueIndex(filter.getGroups(), new Function<Group, Integer>() {
            @Override public Integer apply(Group group) { return group.getId(); }
        });
        
        BiMap<LitePoint, SimpleDevice> points = attributeService.getPoints(deviceCollection.getDeviceList(), filter.getAttribute()).inverse();
        Map<Integer, LitePoint> pointIdToPoint = new HashMap<>();
        for (LitePoint point : points.keySet()) {
            pointIdToPoint.put(point.getLiteID(), point);
        }
        
        Collection<Integer> pointIds = Collections2.transform(points.keySet(), LitePoint.ID_FUNCTION);
        Set<? extends PointValueQualityHolder> values = dds.getPointValue(Sets.newHashSet(pointIds));
        
        for (PointValueQualityHolder pvqh : values) {
            LitePoint lp = pointIdToPoint.get(pvqh.getId());
            Group group = groups.get(lp.getStateGroupID());
            if ((int)pvqh.getValue() == group.getState()) {
                paoIds.add(points.get(lp).getDeviceId());
            }
        }
        
        return paoIds;
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
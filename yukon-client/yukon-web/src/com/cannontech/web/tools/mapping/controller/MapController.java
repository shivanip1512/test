package com.cannontech.web.tools.mapping.controller;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.PaoLocationDao.FeaturePropertyType;
import com.cannontech.web.tools.mapping.service.PaoLocationService;

@Controller
public class MapController {
    
    @Autowired private PaoLocationService paoLocationService;
    
    @RequestMapping("/map")
    public String map(ModelMap model) {
        
        return "map/map.jsp";
    }

    @RequestMapping(value="/map", params="collectionType")
    public String map(ModelMap model, DeviceCollection deviceCollection) {
        
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("deviceCollection", deviceCollection);
        
        return "map/map.jsp";
    }
    
    @RequestMapping("/map/locations")
    public @ResponseBody FeatureCollection locations(DeviceCollection deviceCollection) {
        
//        FeatureCollection locations = paoLocationService.getLastLocationsAsGeoJson(collection.getDeviceList());
        FeatureCollection locations = getFakeLocations(deviceCollection);
        
        return locations;
    }
    
    private FeatureCollection getFakeLocations(DeviceCollection deviceCollection) {
        
        FeatureCollection collection = new FeatureCollection();
        
        Map<String, Object> crsProperties = new HashMap<>();
        crsProperties.put("name", "EPSG:4326"); // Yukon is assuming WSG84 (EPSG:4326)
        Crs crs = new Crs();
        crs.setProperties(crsProperties);
        
        collection.setCrs(crs);
        
        DecimalFormat df = new DecimalFormat("##.######");
        
        for (SimpleDevice device : deviceCollection.getDeviceList()) {
            
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
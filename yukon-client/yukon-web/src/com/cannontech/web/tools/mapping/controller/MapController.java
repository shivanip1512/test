package com.cannontech.web.tools.mapping.controller;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.web.tools.mapping.dao.LocationDao;
import com.cannontech.web.tools.mapping.model.Location;

@Controller
public class MapController {
    
    @Autowired LocationDao locationDao;
    
    @RequestMapping("/map")
    public String map(ModelMap model) {
        
        return "map/map.jsp";
    }

    @RequestMapping(value="/map", params="collectionType")
    public String map(ModelMap model, DeviceCollection deviceCollection) {
        
//        Set<Location> locations = locationDao.getLastLocations(collection.getDeviceList());
        Set<Location> locations = getFakeLocations(deviceCollection);
        
        model.addAttribute("locations", locations);
        model.addAttribute("collection", deviceCollection);
        
        return "map/map.jsp";
    }
    
    @RequestMapping("/map/locations")
    public @ResponseBody Set<Location> locations(DeviceCollection deviceCollection) {
        
//        Set<Location> locations = locationDao.getLastLocations(collection.getDeviceList());
        Set<Location> locations = getFakeLocations(deviceCollection);
        
        return locations;
    }
    
    private Set<Location> getFakeLocations(DeviceCollection deviceCollection) {
        
        DecimalFormat df = new DecimalFormat("##.######");
        
        Set<Location> locations = new HashSet<>();
        
        for (SimpleDevice device : deviceCollection.getDeviceList()) {
            Location loc = new Location();
            loc.setPaoIdentifier(device.getPaoIdentifier());
            loc.setTimestamp(Instant.now().minus(Duration.standardDays((long) ((Math.random() * (10 - 1)) + 1))));
            loc.setLatitude(Double.parseDouble(df.format((Math.random() * (45.3 - 45.0)) + 45.0)));
            loc.setLongitude(Double.parseDouble(df.format(((Math.random() * (93.5 - 93.0)) + 93.0) * -1)));
            
            locations.add(loc);
        }
        
        return locations;
    }
}
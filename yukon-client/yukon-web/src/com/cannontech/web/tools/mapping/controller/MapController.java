package com.cannontech.web.tools.mapping.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
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
    public String map(ModelMap model, DeviceCollection collection) {
        
        Set<Location> locations = locationDao.getLastLocations(collection.getDeviceList());
        model.addAttribute("locations", locations);
        
        return "map/map.jsp";
    }
    
    @RequestMapping("/map/locations")
    public @ResponseBody Set<Location> locations(DeviceCollection collection) {
        
        Set<Location> locations = locationDao.getLastLocations(collection.getDeviceList());
        
        return locations;
    }
    
}
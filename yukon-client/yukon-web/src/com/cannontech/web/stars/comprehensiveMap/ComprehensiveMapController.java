package com.cannontech.web.stars.comprehensiveMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.web.tools.mapping.model.NetworkMap;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter;
import com.cannontech.web.tools.mapping.model.NetworkMapFilter.ColorCodeBy;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.google.common.collect.Lists;


@RequestMapping("/comprehensiveMap/*")
@Controller
public class ComprehensiveMapController {
    
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private NmNetworkService nmNetworkService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private MeterDao meterDao;
    
    @GetMapping("home")
    public String home(ModelMap model) {
        
        NetworkMapFilter filter = new NetworkMapFilter();
        model.addAttribute("filter", filter);                                                                
                                                                                                              
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());                                                                                                
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);
        
        model.addAttribute("colorCodeByOptions", ColorCodeBy.values());
        
        return "comprehensiveMap/map.jsp";
    }
    
    @GetMapping("filter")
    public @ResponseBody Map<String, Object> filter(@ModelAttribute("filter") NetworkMapFilter filter) {
        Map<String, Object> json = new HashMap<>();
        NetworkMap map = nmNetworkService.getNetworkMap(filter);
        json.put("map",  map);
        return json;
    }
    
    @GetMapping("search")
    public @ResponseBody Map<String, Object> searchForNode(String searchText, @ModelAttribute("filter") NetworkMapFilter filter) {
        Map<String, Object> json = new HashMap<>();
        NetworkMap map = nmNetworkService.getNetworkMap(filter);
        Map<String, FeatureCollection> collections = map.getMappedDevices();
        for (FeatureCollection features : collections.values()) {
            // Search the devices on the map by Sensor SN and Meter Number
            for (Feature feature : features.getFeatures()) {
                PaoIdentifier pao = (PaoIdentifier) feature.getProperties().get("paoIdentifier");
                try {
                    RfnDevice rfnDevice = rfnDeviceDao.getDeviceForId(pao.getPaoId());
                    String ssn = rfnDevice.getRfnIdentifier().getSensorSerialNumber();
                    if (ssn.equals(searchText)) {
                        json.put("paoId", pao.getPaoId());
                        return json;
                    }
                } catch (NotFoundException e) {
                    //ignore
                }
                if(pao.getPaoType().isMeter()) {
                    YukonMeter meter = meterDao.getForId(pao.getPaoId());
                    if (meter.getMeterNumber().equals(searchText)) {
                        json.put("paoId", pao.getPaoId());
                        return json;
                    }
                }
            }
        }
        return json;
    }
    
}
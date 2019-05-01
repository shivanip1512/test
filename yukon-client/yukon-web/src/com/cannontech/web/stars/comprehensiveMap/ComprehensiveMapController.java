package com.cannontech.web.stars.comprehensiveMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
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
    
}
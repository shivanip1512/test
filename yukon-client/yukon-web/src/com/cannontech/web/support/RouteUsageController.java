package com.cannontech.web.support;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.database.data.route.RouteUsageHelper;

@Controller
public class RouteUsageController {

    @RequestMapping("/routeUsage")
    public String routeUsage(ModelMap model) {
        
        RouteUsageHelper helper = new RouteUsageHelper(); 
        model.addAttribute("routeTables", helper.getRouteUsage());
        
        return "routeUsage.jsp";
    }
    
}
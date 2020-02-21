package com.cannontech.web.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.user.YukonUserContext;

@Controller
public class SiteMapController {
    
    @Autowired private SiteMapHelper siteMapHelper;

    @RequestMapping("/sitemap")
    public String view(ModelMap model, YukonUserContext context) {
        model.addAttribute("pageMap", siteMapHelper.getSiteMap(context));
        return "siteMap.jsp";
    }
}
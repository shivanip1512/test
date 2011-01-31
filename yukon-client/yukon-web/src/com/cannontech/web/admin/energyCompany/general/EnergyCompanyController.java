package com.cannontech.web.admin.energyCompany.general;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

@Controller
public class EnergyCompanyController {
    
    private StarsDatabaseCache starsDatabaseCache;
    
    /* Energy Company Operations Page*/
    @RequestMapping("/energyCompany/home")
    public String home(YukonUserContext userContext, ModelMap modelMap, Integer ecId) {
        List<LiteStarsEnergyCompany> companies = Lists.newArrayList();
        
        if (ecId != null) {
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
            companies.addAll(ECUtils.getAllDescendants(energyCompany));
        }
        
        modelMap.addAttribute("companies", companies);
        
        return "energyCompany/home.jsp";
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
}
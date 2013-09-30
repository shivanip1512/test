package com.cannontech.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.system.GlobalSettingSubCategory;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class WeatherController {

    @RequestMapping("/config/weather")
    public String theme(ModelMap model, YukonUserContext context) {
        
        model.addAttribute("category", GlobalSettingSubCategory.WEATHER);
        
        return "config/weather.jsp";
    }

}
package com.cannontech.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
@RequestMapping("/config/*")
public class YukonConfigurationController {
    
    @RequestMapping
    public String view(ModelMap model, YukonUserContext context) {
        
        
        return "config/view.jsp";
    }
    
}
package com.cannontech.web.dr;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.DEMAND_RESPONSE)
public class EcobeeController {

    @RequestMapping(value="/ecobee", method=RequestMethod.GET)
    public String details(ModelMap model) {
        
        return "dr/ecobee/details.jsp";
    }
}

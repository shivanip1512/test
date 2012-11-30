package com.cannontech.web.stars.dr.consumer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_ACCOUNT_GENERAL)
@Controller
public class SeasonalOverrideController extends AbstractConsumerController {
        
    private static final String view = "consumer/seasonalOverride.jsp";
       
    @RequestMapping(value = "/consumer/so", method = RequestMethod.GET)
    public String view(ModelMap model) {
        
        
        
        return view;
    }
        
}
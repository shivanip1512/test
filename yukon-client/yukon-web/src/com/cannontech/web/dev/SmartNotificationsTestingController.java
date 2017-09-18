package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;


@Controller
public class SmartNotificationsTestingController {
    
    @RequestMapping("smartNotificationsSimulator")
    public String smartNotificationsSimulator() {
        return "smartNotificationsSimulator.jsp";
    }
    
    @RequestMapping("clearAllSubscriptions")
    public String clearAllSubscriptions() {
        //TODO: Clear out all subscriptions
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping("createEvents")
    public String createEvents() {
        //TODO: Create Events for subscriptions
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping(value="saveSubscription", method=RequestMethod.POST)
    public String saveSmartNotificationsSubscription(@ModelAttribute("subscription") SmartNotificationSubscription subscription, @RequestParam int userGroupId) throws Exception {
        //TODO: Save subscription
        return "redirect:smartNotificationsSimulator";
    }
}

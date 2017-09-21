package com.cannontech.web.dev;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.common.flashScope.FlashScope;


@Controller
public class SmartNotificationsTestingController {
    
    @RequestMapping("smartNotificationsSimulator")
    public String smartNotificationsSimulator() {
        return "smartNotificationsSimulator.jsp";
    }
    
    @RequestMapping("clearAllSubscriptions")
    public String clearAllSubscriptions(FlashScope flash) {
        //TODO: Clear out all subscriptions
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("All Subscriptions have been cleared."));
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping("clearAllEvents")
    public String clearAllEvents(FlashScope flash) {
        //TODO: Clear out all events
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("All Events have been cleared."));
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping("createRealEvents")
    public String createRealEvents(FlashScope flash) {
        //TODO: Create Events for subscriptions
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Events have been created."));
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping("createEvents")
    public String createEvents(@RequestParam int waitTime, @RequestParam int eventsPerMessage, FlashScope flash) {
        //TODO: Create Events for subscriptions
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Test events have been created."));
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping(value="saveSubscription", method=RequestMethod.POST)
    public String saveSmartNotificationsSubscription(@ModelAttribute("subscription") SmartNotificationSubscription subscription, 
                                                     @RequestParam int userGroupId) throws Exception {
        //TODO: Save subscription
        return "redirect:smartNotificationsSimulator";
    }
}

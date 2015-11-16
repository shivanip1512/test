package com.cannontech.web.common.curtailment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.service.CustomerEventService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

@Controller
public class ProgramEventsController {
    
    private static final Logger log = YukonLogManager.getLogger(ProgramEventsController.class);
    
    @Autowired private CustomerEventService customerEventService;
    @Autowired private DateFormattingService dateFormattingService;
    
    @RequestMapping("/cc/programs-events")
    public String events(LiteYukonUser user, ModelMap model, YukonUserContext userContext) {
        
        List<BaseEvent> currentEvents = customerEventService.getCurrentEvents(user);
        List<BaseEvent> pendingEvents = customerEventService.getPendingEvents(user);
        List<BaseEvent> recentEvents = customerEventService.getRecentEvents(user);
        
        model.addAttribute("currentEvents", currentEvents);
        model.addAttribute("pendingEvents", pendingEvents);
        model.addAttribute("recentEvents", recentEvents);
        Map<String, List<BaseEvent>> recentEventsCollection = new HashMap<>();
        recentEventsCollection.put("recentEventsCollection", recentEvents);
        model.addAttribute("recentEventsCollection", recentEventsCollection);
        
        return "/cc/program-events.jsp";
    }
    
    @RequestMapping("/cc/programs-events/details/{id}")
    public String details(LiteYukonUser user, ModelMap model, YukonUserContext userContext, 
            @PathVariable String id) {
        
        
        model.addAttribute("eventDetail", null);
        return "/cc/program-event-details.jsp";
    }
    
}
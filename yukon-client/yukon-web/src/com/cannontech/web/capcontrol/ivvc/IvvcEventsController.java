package com.cannontech.web.capcontrol.ivvc;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.util.TimeRange;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.service.IvvcEventsService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableList;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class IvvcEventsController {
    
    @Autowired private IvvcEventsService eventsService;
    
    /** Returns a list of events sorted by most recent descending as JSON. */
    @RequestMapping(value="zones/{id}/events")
    public @ResponseBody List<Map<String, Object>> getZoneEvents(
        @PathVariable int id, 
        @RequestParam TimeRange range, 
        YukonUserContext userContext) {
        
        List<Integer> regulatorIds = eventsService.getRegulatorsForZone(id);

        List<Map<String, Object>> events = eventsService.getEventsForRegulatorIds(regulatorIds, range, userContext);
        
        return events;
    }
    
    /** Returns a list of events sorted by most recent descending as JSON. */
    @RequestMapping(value="buses/{id}/events")
    public @ResponseBody List<Map<String, Object>> getBusEvents(
        @PathVariable int id, 
        @RequestParam TimeRange range, 
        YukonUserContext userContext) {
        
        List<Integer> regulatorIds = eventsService.getRegulatorsForBus(id);
        
        List<Map<String, Object>> events = eventsService.getEventsForRegulatorIds(regulatorIds, range, userContext);
        
        return events;
    }
    
    /** Returns a list of events sorted by most recent descending as JSON. */
    @RequestMapping(value="regulators/{id}/events")
    public @ResponseBody List<Map<String, Object>> getRegulatorEvents(
        @PathVariable int id, 
        @RequestParam TimeRange range, 
        YukonUserContext userContext) {
        
        List<Map<String, Object>> events = eventsService.getEventsForRegulatorIds(ImmutableList.of(id), range, userContext);
        
        return events;
    }
}
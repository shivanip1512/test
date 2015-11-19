package com.cannontech.web.capcontrol.ivvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.common.util.TimeRange;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.service.IvvcEventsService;
import com.cannontech.web.capcontrol.ivvc.service.IvvcEventsService.IvvcEvent;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class IvvcEventsController {
    
    @Autowired private IvvcEventsService eventsService;
    @Autowired private SubstationBusDao busDao;
    @Autowired private ZoneService zoneService;
    
    @RequestMapping(value = "zones/{id}/events")
    public @ResponseBody List<Map<String, Object>> getZoneEvents(@PathVariable int id, @RequestParam TimeRange range,
            YukonUserContext context) {

        List<Integer> regulatorIds = zoneService.getRegulatorsForZone(id);
        Zone zone = zoneService.getZoneById(id);

        List<IvvcEvent> regulatorEvents = eventsService.getRegulatorEvents(regulatorIds, range, context);
        List<IvvcEvent> capBankEvents = eventsService.getCapBankEvents(ImmutableList.of(id), range);
        List<IvvcEvent> commStatusEvents = eventsService.getCommStatusEvents(zone.getSubstationBusId(), range);

        List<IvvcEvent> allEvents = new ArrayList<IvvcEvent>();
        allEvents.addAll(regulatorEvents);
        allEvents.addAll(capBankEvents);
        allEvents.addAll(commStatusEvents);

        return getSortedEvents(allEvents);
    }
    
    @RequestMapping(value = "buses/{id}/events")
    public @ResponseBody List<Map<String, Object>> getBusEvents(@PathVariable int id, @RequestParam TimeRange range,
            YukonUserContext context) {

        List<Integer> regulatorIds = busDao.getRegulatorsForBus(id);
        List<Zone> busZones = zoneService.getZonesBySubBusId(id);
        List<Integer> zoneIds = Lists.newArrayList(Collections2.transform(busZones, new Function<Zone, Integer>() {
            @Override
            public Integer apply(Zone zone) {
                return zone.getId();
            }
        }));

        List<IvvcEvent> regulatorEvents = eventsService.getRegulatorEvents(regulatorIds, range, context);
        List<IvvcEvent> capBankEvents = eventsService.getCapBankEvents(zoneIds, range);
        List<IvvcEvent> commStatusEvents = eventsService.getCommStatusEvents(id, range);
        
        List<IvvcEvent> allEvents = new ArrayList<IvvcEvent>();
        allEvents.addAll(regulatorEvents);
        allEvents.addAll(capBankEvents);
        allEvents.addAll(commStatusEvents);

        return getSortedEvents(allEvents);
    }
    
    @RequestMapping(value = "regulators/{id}/events")
    public @ResponseBody List<Map<String, Object>> getRegulatorEvents(@PathVariable int id,
            @RequestParam TimeRange range, YukonUserContext context) {

        List<IvvcEvent> regulatorEvents = eventsService.getRegulatorEvents(ImmutableList.of(id), range, context);

        return getSortedEvents(regulatorEvents);
    }
    
    /** Returns a list of events sorted by most recent descending as JSON. */
    private List<Map<String, Object>> getSortedEvents(List<IvvcEvent> ivvcEvents) {
        Collections.sort(ivvcEvents, new Comparator<IvvcEvent>() {
            @Override
            public int compare(IvvcEvent o1, IvvcEvent o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });
        List<Map<String, Object>> events = new ArrayList<>();
        for (IvvcEvent ivvcEvent : ivvcEvents) {
            events.add(ivvcEvent.getEventMap());
        }
        return events;
    }
}
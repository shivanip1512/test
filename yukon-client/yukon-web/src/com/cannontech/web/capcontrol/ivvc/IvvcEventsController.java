package com.cannontech.web.capcontrol.ivvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.capcontrol.dao.RegulatorEventsDao;
import com.cannontech.capcontrol.model.RegulatorEvent;
import com.cannontech.capcontrol.model.RegulatorEvent.EventType;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.TimeRange;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.regulator.setup.service.RegulatorMappingService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.WebUtilityService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class IvvcEventsController {
    
    @Autowired private CapControlCache ccCache;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private RegulatorEventsDao eventsDao;
    @Autowired private VoltageRegulatorService regulatorService;
    @Autowired private RegulatorMappingService mappingService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ZoneService zoneService;
    @Autowired private WebUtilityService webUtil;
    
    private static final Map<RegulatorEvent.EventType, String> classNameForEventType;
    
    static {
        ImmutableMap.Builder<EventType, String> builder = ImmutableMap.builder();
        
        builder.put(EventType.TAP_UP, "icon-arrow-up-green");
        builder.put(EventType.TAP_DOWN, "icon-arrow-down-orange");
        builder.put(EventType.INCREASE_SETPOINT, "icon-arrow-up-green");
        builder.put(EventType.DECREASE_SETPOINT, "icon-arrow-down-orange");
        builder.put(EventType.INTEGRITY_SCAN, "icon-transmit-blue");
        builder.put(EventType.ENABLE_REMOTE_CONTROL, "icon-accept");
        builder.put(EventType.DISABLE_REMOTE_CONTROL, "icon-delete");
        
        classNameForEventType = builder.build();
    }
    
    private static final String eventTypeBaseKey = "yukon.web.modules.capcontrol.ivvc.eventType";
    
    /** Returns a list of events sorted by most recent descending as JSON. */
    @RequestMapping(value="ivvc/zones/{id}/events")
    public @ResponseBody List<Map<String, Object>> getZoneEvents(@PathVariable int id, 
            @RequestParam TimeRange range, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        List<Integer> regulatorIds = getRegulatorsForZone(id);
        
        
        List<Map<String, Object>> events = new ArrayList<>();
        for (RegulatorEvent re : eventsDao.getForIds(regulatorIds, range)) {
            
            Map<String, Object> event = new HashMap<>();
            
            event.put("id", re.getId());
            event.put("timestamp", re.getTimestamp().getMillis());
            event.put("user", re.getUserName());
            
            String iconClass = classNameForEventType.get(re.getType());
            event.put("icon", iconClass);
            
            String phaseString = accessor.getMessage(re.getPhase());
            String key = eventTypeBaseKey + "." + re.getType().name();
            String message = accessor.getMessage(key, phaseString);
            event.put("message", message);
            
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(re.getRegulatorId());
            event.put("deviceName", pao.getPaoName());
            
            events.add(event);
        }
        
        return events;
    }
    
    /** Returns a list of events sorted by most recent descending as JSON. */
    @RequestMapping(value="ivvc/buses/{id}/events")
    public @ResponseBody List<Map<String, Object>> getBusEvents(@PathVariable int id, 
        @RequestParam TimeRange range, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        List<Integer> regulatorIds = getRegulatorsForBus(id);
        
        
        List<Map<String, Object>> events = new ArrayList<>();
        for (RegulatorEvent re : eventsDao.getForIds(regulatorIds, range)) {
            
            Map<String, Object> event = new HashMap<>();
            
            event.put("id", re.getId());
            event.put("timestamp", re.getTimestamp().getMillis());
            event.put("user", re.getUserName());
            
            String iconClass = classNameForEventType.get(re.getType());
            event.put("icon", iconClass);
            
            String phaseString = accessor.getMessage(re.getPhase());
            String key = eventTypeBaseKey + "." + re.getType().name();
            String message = accessor.getMessage(key, phaseString);
            event.put("message", message);
            
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(re.getRegulatorId());
            event.put("deviceName", pao.getPaoName());
            
            events.add(event);
        }
        
        return events;
    }
    
    private List<Integer> getRegulatorsForBus(int id) {
        
        List<Integer> regulatorIds = new ArrayList<>();
        
        SubBus bus = ccCache.getSubBus(id);
        
        if (bus != null) {
            
            List<Zone> busZones = zoneService.getZonesBySubBusId(bus.getCcId());
            
            for (Zone zone : busZones) {
                List<RegulatorToZoneMapping> mappings = zone.getRegulators();
                
                for (RegulatorToZoneMapping mapping : mappings) {
                    regulatorIds.add(mapping.getRegulatorId());
                }
                
            }
                
        }        
        
        return regulatorIds;
    }
    
    private List<Integer> getRegulatorsForZone(int id) {
        
        List<Integer> regulatorIds = new ArrayList<>();
            
        Zone zone = zoneService.getZoneById(id);
            
        List<RegulatorToZoneMapping> mappings = zone.getRegulators();
        
        for (RegulatorToZoneMapping mapping : mappings) {
            regulatorIds.add(mapping.getRegulatorId());
        }
        
        return regulatorIds;
    }
}
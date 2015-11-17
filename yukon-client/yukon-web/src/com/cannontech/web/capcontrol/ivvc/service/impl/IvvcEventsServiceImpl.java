package com.cannontech.web.capcontrol.ivvc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.RegulatorEventsDao;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.RegulatorEvent;
import com.cannontech.capcontrol.model.RegulatorEvent.EventType;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.TimeRange;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.service.IvvcEventsService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;

@Service
public class IvvcEventsServiceImpl implements IvvcEventsService {
    
    @Autowired private IDatabaseCache dbCache;
    @Autowired private RegulatorEventsDao eventsDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ZoneService zoneService;

    private static final String eventTypeBaseKey = "yukon.web.modules.capcontrol.ivvc.eventType";
        
    private static final Map<RegulatorEvent.EventType, String> classNameForRegulatorEventType;

    enum OddEven {
        ODD, EVEN
    }
     
    static {
        ImmutableMap.Builder<EventType, String> regulatorBuilder = ImmutableMap.builder();
        
        regulatorBuilder.put(EventType.TAP_UP, "icon-arrow-up-green");
        regulatorBuilder.put(EventType.TAP_DOWN, "icon-arrow-down-orange");
        regulatorBuilder.put(EventType.INCREASE_SETPOINT, "icon-arrow-up-green");
        regulatorBuilder.put(EventType.DECREASE_SETPOINT, "icon-arrow-down-orange");
        regulatorBuilder.put(EventType.INTEGRITY_SCAN, "icon-transmit-blue");
        regulatorBuilder.put(EventType.ENABLE_REMOTE_CONTROL, "icon-accept");
        regulatorBuilder.put(EventType.DISABLE_REMOTE_CONTROL, "icon-delete");
        
        classNameForRegulatorEventType = regulatorBuilder.build();
    }
   
    @Override
    public  List<IvvcEvent> getRegulatorEvents(Iterable<Integer> regulatorIds, TimeRange range,
            YukonUserContext context) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(context);

        List<RegulatorEvent> regulatorEvents = eventsDao.getForIds(regulatorIds, range);

        List<IvvcEvent> events = new ArrayList<>();
        for (RegulatorEvent re : regulatorEvents) {
            String phaseString = accessor.getMessage(re.getPhase());
            String key = eventTypeBaseKey + "." + re.getType().name();
            String message = accessor.getMessage(key, phaseString);
            String iconClass = classNameForRegulatorEventType.get(re.getType());
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(re.getRegulatorId());
                    
            IvvcEvent  event = new IvvcEvent();
            event.setId("regulatorevent_"+re.getId());
            event.setDeviceName(pao.getPaoName());
            event.setIcon(iconClass);
            event.setUser(re.getUserName());
            event.setMessage(message);
            event.setTimestamp(re.getTimestamp());
            events.add(event);
        }
        return events;
    }
    
    @Override
    public List<IvvcEvent> getCapBankEvents(List<Integer> zoneIds, TimeRange range) {

        Map<OddEven, String> iconClassNames = new HashMap<>();
        iconClassNames.put(OddEven.ODD, "icon-arrow-down-orange");
        iconClassNames.put(OddEven.EVEN, "icon-arrow-up-green");
        List<CcEvent> capBankEvents = zoneService.getLatestCapBankEvents(zoneIds, range);
        return getIvvcEvents(capBankEvents, iconClassNames);
    }
    
    @Override
    public List<IvvcEvent> getCommStatusEvents(int subBusId, TimeRange range) {

        Map<OddEven, String> iconClassNames = new HashMap<>();
        iconClassNames.put(OddEven.ODD, "icon-arrow-up-green");
        iconClassNames.put(OddEven.EVEN, "icon-arrow-down-orange");
        List<CcEvent> capBankEvents = zoneService.getLatestCommStatusEvents(subBusId, range);
        return getIvvcEvents(capBankEvents, iconClassNames);
    }
    
    private List<IvvcEvent> getIvvcEvents(List<CcEvent> ccEvents, Map<OddEven, String> iconClassNames) {
        List<IvvcEvent> events = new ArrayList<>();
        for (CcEvent cce : ccEvents) {
            IvvcEvent event = new IvvcEvent();
            event.setId("ccevent_"+cce.getId());
            event.setDeviceName(cce.getDeviceName());
            if (cce.getValue() % 2 == 0) {
                event.setIcon(iconClassNames.get(OddEven.EVEN));
            } else {
                event.setIcon(iconClassNames.get(OddEven.ODD));
            }
            event.setTimestamp(cce.getDateTime());
            event.setUser(cce.getUserName());
            event.setMessage(cce.getText());
            events.add(event);
        }
        return events;
    }
    
   public class IvvcEvent {
        private String id;
        private Instant timestamp;
        private String message;
        private String deviceName;
        private String user;
        private String icon;

        public Instant getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
      
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        
        /* Returns event as JSON */
        public Map<String, Object> getEventMap() {
            Map<String, Object> event = new HashMap<>();
            event.put("id", id);
            event.put("timestamp", timestamp.getMillis());
            event.put("message", message);
            event.put("deviceName", deviceName);
            event.put("user", user);
            event.put("icon", icon);
            return event;
        }
    }
}
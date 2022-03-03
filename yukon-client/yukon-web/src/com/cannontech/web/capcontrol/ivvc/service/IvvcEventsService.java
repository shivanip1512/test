package com.cannontech.web.capcontrol.ivvc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.util.TimeRange;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.i18n.IconSvg;

public interface IvvcEventsService {
    
    public class IvvcEvent {
        private String id;
        private Instant timestamp;
        private String message;
        private String deviceName;
        private String user;
        private String icon;
        private String iconSvg;

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
            event.put("iconSvg", getIconSvg());
            return event;
        }

		public String getIconSvg() {
			return IconSvg.getIconForClass(icon).getSvg();
		}
    }
    
    List<IvvcEvent> getCapBankEvents(List<Integer> zoneIds, TimeRange range);

    List<IvvcEvent> getCommStatusEvents(int subBusId, TimeRange range);

    List<IvvcEvent> getRegulatorEvents(Iterable<Integer> regulatorIds, TimeRange range, YukonUserContext context);
}
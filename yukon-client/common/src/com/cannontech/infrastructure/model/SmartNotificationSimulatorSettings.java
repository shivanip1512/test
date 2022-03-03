package com.cannontech.infrastructure.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.smartNotification.model.SmartNotificationEventType;

public class SmartNotificationSimulatorSettings implements Serializable {
    
    private int dailyDigestHour, eventsPerType, eventsPerMessage, waitTimeSec;
    private boolean allTypes;
    private SmartNotificationEventType type;
    private String parameter;
    
    public SmartNotificationSimulatorSettings(int dailyDigestHour, boolean allTypes, SmartNotificationEventType type, String parameter, 
                                              int eventsPerType, int eventsPerMessage, int waitTimeSec) {
        this.dailyDigestHour = dailyDigestHour;
        this.setAllTypes(allTypes);
        this.setType(type);
        this.setParameter(parameter);
        this.eventsPerType = eventsPerType;
        this.eventsPerMessage = eventsPerMessage;
        this.waitTimeSec = waitTimeSec;
    }
    
    public int getDailyDigestHour() {
        return dailyDigestHour;
    }
    public void setDailyDigestHour(int dailyDigestHour) {
        this.dailyDigestHour = dailyDigestHour;
    }
    public int getEventsPerType() {
        return eventsPerType;
    }
    public void setEventsPerType(int eventsPerType) {
        this.eventsPerType = eventsPerType;
    }
    public int getEventsPerMessage() {
        return eventsPerMessage;
    }
    public void setEventsPerMessage(int eventsPerMessage) {
        this.eventsPerMessage = eventsPerMessage;
    }
    public int getWaitTimeSec() {
        return waitTimeSec;
    }
    public void setWaitTimeSec(int waitTimeSec) {
        this.waitTimeSec = waitTimeSec;
    }

    public SmartNotificationEventType getType() {
        return type;
    }

    public void setType(SmartNotificationEventType type) {
        this.type = type;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public boolean isAllTypes() {
        return allTypes;
    }

    public void setAllTypes(boolean allTypes) {
        this.allTypes = allTypes;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
 
}

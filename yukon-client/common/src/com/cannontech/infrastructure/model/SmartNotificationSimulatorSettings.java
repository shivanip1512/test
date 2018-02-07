package com.cannontech.infrastructure.model;

import java.io.Serializable;

public class SmartNotificationSimulatorSettings implements Serializable {
    
    private int dailyDigestHour, userGroupId, eventsPerType, eventsPerMessage, waitTimeSec;
    private boolean generateTestEmail;
    
    public SmartNotificationSimulatorSettings(int dailyDigestHour, int userGroupId,
                                              boolean generateTestEmail, int eventsPerType,
                                              int eventsPerMessage, int waitTimeSec) {
        this.dailyDigestHour = dailyDigestHour;
        this.userGroupId = userGroupId;
        this.generateTestEmail = generateTestEmail;
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
    public int getUserGroupId() {
        return userGroupId;
    }
    public void setUserGroupId(int userGroupId) {
        this.userGroupId = userGroupId;
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
    public boolean isGenerateTestEmail() {
        return generateTestEmail;
    }
    public void setGenerateTestEmail(boolean generateTestEmail) {
        this.generateTestEmail = generateTestEmail;
    }
 
}

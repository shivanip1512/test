package com.cannontech.cc.service.builder;

import java.util.Date;
import java.util.TimeZone;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;

public class CurtailmentBuilder extends EventBuilderBase {
    private TimeZone timeZone;
    private CurtailmentEvent event;
    
    public Integer getEventDuration() {
        return event.getDuration();
    }
    public void setEventDuration(Integer duration) {
        event.setDuration(duration);
    }
    public String getMessage() {
        return event.getMessage();
    }
    public void setMessage(String message) {
        event.setMessage(message);
    }
    public Date getNotificationTime() {
        return event.getNotificationTime();
    }
    public void setNotificationTime(Date notificationTime) {
        event.setNotificationTime(notificationTime);
    }
    public Date getStartTime() {
        return event.getStartTime();
    }
    public void setStartTime(Date startTime) {
        event.setStartTime(startTime);
    }
    public void setTimeZone(TimeZone tz) {
        timeZone = tz;
    }
    public TimeZone getTimeZone() {
        return timeZone;
    }
    public Program getProgram() {
        return event.getProgram();
    }
    public void setProgram(Program program) {
        event.setProgram(program);
    }
    public CurtailmentEvent getEvent() {
        return event;
    }
    public void setEvent(CurtailmentEvent event) {
        this.event = event;
    }
    
}

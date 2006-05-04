package com.cannontech.cc.service.builder;

import java.util.Date;
import java.util.TimeZone;

import com.cannontech.cc.model.Program;

public class CurtailmentBuilder extends EventBuilderBase {
    private Date notificationTime = new Date();
    private Date startTime = new Date();
    private Integer eventDuration = new Integer(0);
    private String message = "";
    private TimeZone timeZone;
    private Program program;
    
    public Integer getEventDuration() {
        return eventDuration;
    }
    public void setEventDuration(Integer duration) {
        this.eventDuration = duration;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Date getNotificationTime() {
        return notificationTime;
    }
    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public void setTimeZone(TimeZone tz) {
        timeZone = tz;
    }
    public TimeZone getTimeZone() {
        return timeZone;
    }
    public Program getProgram() {
        return program;
    }
    public void setProgram(Program program) {
        this.program = program;
    }
    
}

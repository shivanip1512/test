package com.cannontech.cc.service.builder;

import java.util.Date;
import java.util.TimeZone;

import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.Program;

public class AccountingBuilder extends EventBuilderBase {
    private TimeZone timeZone;
    private AccountingEvent event;
    
    public Integer getEventDuration() {
        return event.getDuration();
    }
    public void setEventDuration(Integer duration) {
        event.setDuration(duration);
    }
    public String getReason() {
        return event.getReason();
    }
    public void setReason(String reason) {
        event.setReason(reason);
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
    public AccountingEvent getEvent() {
        return event;
    }
    public void setEvent(AccountingEvent event) {
        this.event = event;
    }
    
}

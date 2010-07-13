package com.cannontech.cc.model;

import java.util.Date;

import com.cannontech.common.util.TimeUtil;

public class AccountingEvent extends BaseEvent {
    private Integer id;
    private Program program;
    private Date startTime;
    private String reason;
    private Integer duration;
    private Integer identifier = new Integer(0);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Date getNotificationTime() {
        return startTime;
    }
    
    public Program getProgram() {
        return program;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Integer eventDuration) {
        this.duration = eventDuration;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public Date getStopTime() {
        return TimeUtil.addMinutes(getStartTime(), getDuration());
    }
    
    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "AccountingEvent " + getDisplayName() + " [" + id + "]";
    }

    @Override 
    public String getStateDescription() {
        return "NORMAL";
    }

}

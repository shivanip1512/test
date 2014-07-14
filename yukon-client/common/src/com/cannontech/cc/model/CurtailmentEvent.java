package com.cannontech.cc.model;

import java.util.Date;

import com.cannontech.cc.service.CurtailmentEventState;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.support.Identifiable;

public class CurtailmentEvent extends BaseEvent implements Identifiable{
    private Program program;
    private Date startTime;
    private Date notificationTime;
    private String message;
    private Integer duration;
    private Integer id;
    private Integer identifier = new Integer(0);
    private CurtailmentEventState state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    
    public void setState(CurtailmentEventState state) {
        this.state = state;
    }

    public CurtailmentEventState getState() {
        return state;
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
    public String getStateDescription() {
        return state.toString();
    }

    @Override
    public String toString() {
        return "CurtailmentEvent [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }

}

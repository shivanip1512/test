package com.cannontech.cc.service.builder;

import java.util.Date;

import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.common.util.TimeUtil;

public class CurtailmentChangeBuilder {
    private CurtailmentEvent originalEvent;
    private Date newStartTime;
    private int newLength;
    private String newMessage;
    

    public CurtailmentChangeBuilder(CurtailmentEvent originalEvent) {
        this.originalEvent = originalEvent;
        newStartTime = originalEvent.getStartTime();
        newLength = originalEvent.getDuration();
    }

    public int getNewLength() {
        return newLength;
    }
    public void setNewLength(int newLength) {
        this.newLength = newLength;
    }
    public Date getNewStartTime() {
        return newStartTime;
    }
    public void setNewStartTime(Date newStartTime) {
        this.newStartTime = newStartTime;
    }
    public CurtailmentEvent getOriginalEvent() {
        return originalEvent;
    }
    public Date getNewStopTime() {
        return TimeUtil.addMinutes(getNewStartTime(), getNewLength());
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

}

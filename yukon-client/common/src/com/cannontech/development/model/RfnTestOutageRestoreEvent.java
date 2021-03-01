package com.cannontech.development.model;

import com.cannontech.amr.rfn.message.event.RfnConditionType;

public class RfnTestOutageRestoreEvent {
    
    private String deviceGroup;
    private Integer milliseconds = 1;
    private RfnConditionType firstEvent;
    private Boolean firstEventRandom;
    
    public String getDeviceGroup() {
        return deviceGroup;
    }
    public void setDeviceGroup(String deviceGroup) {
        this.deviceGroup = deviceGroup;
    }
    public Integer getMilliseconds() {
        return milliseconds;
    }
    public void setMilliseconds(Integer milliseconds) {
        this.milliseconds = milliseconds;
    }
    public RfnConditionType getFirstEvent() {
        return firstEvent;
    }
    public void setFirstEvent(RfnConditionType firstEvent) {
        this.firstEvent = firstEvent;
    }
    public Boolean getFirstEventRandom() {
        return firstEventRandom;
    }
    public void setFirstEventRandom(Boolean firstEventRandom) {
        this.firstEventRandom = firstEventRandom;
    }
}

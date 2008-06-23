package com.cannontech.stars.xml.serialize;

import java.util.Date;

public class ControlHistory {
    private Date startDateTime;
    private int controlDuration;
    private boolean hasControlDuration;

    public ControlHistory() {
    
    }

    public int getControlDuration() {
        return this.controlDuration;
    } 

    public Date getStartDateTime() {
        return this.startDateTime;
    } 

    public boolean hasControlDuration() {
        return this.hasControlDuration;
    } 

    public void setControlDuration(int controlDuration) {
        this.controlDuration = controlDuration;
        this.hasControlDuration = true;
    } 

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    } 

}

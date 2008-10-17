package com.cannontech.stars.xml.serialize;

import java.util.Date;

public class ControlHistory {
    private Date startDateTime;
    private int controlDuration;
    private boolean hasControlDuration;
    private boolean isCurrentlyControlling = true;

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
    
    public boolean isCurrentlyControlling() {
        return isCurrentlyControlling;
    } 
    
    public void setIsCurrentlyControlling(boolean isCurrentlyControlling) {
        this.isCurrentlyControlling = isCurrentlyControlling;
    }

}

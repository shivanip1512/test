package com.cannontech.web.amr.macsscheduler;

import com.cannontech.messaging.message.macs.ScheduleMessage;

public class MACSScheduleInfo {
    private final ScheduleMessage schedule;
    private final boolean editable;
    
    public MACSScheduleInfo(final ScheduleMessage schedule, final boolean editable) {
        this.schedule = schedule;
        this.editable = editable;
    }
    
    public ScheduleMessage getSchedule() {
        return schedule;
    }
    
    public boolean getEditable() {
        return editable;
    }
    
    public boolean getShowControllable() {
        return (getEditable() &&
               !getUpdatingState() &&
               !getSchedule().getCurrentState().equalsIgnoreCase("Disabled"));
    }
    
    public boolean getShowToggleButton() {
        return (getEditable() &&
               !getUpdatingState());
    }
    
    public boolean getUpdatingState() {
        return getSchedule().getUpdatingState();
    }
    
    public boolean getRunningState() {
        return getSchedule().getCurrentState().equalsIgnoreCase("Running");
    }
    
    public boolean getDisabledState() {
        return getSchedule().getCurrentState().equalsIgnoreCase("Disabled");
    }
    
}

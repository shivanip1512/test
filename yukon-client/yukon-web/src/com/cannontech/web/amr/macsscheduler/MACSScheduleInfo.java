package com.cannontech.web.amr.macsscheduler;

import com.cannontech.message.macs.message.Schedule;

public class MACSScheduleInfo {
    private final Schedule schedule;
    private final boolean editable;
    
    public MACSScheduleInfo(final Schedule schedule, final boolean editable) {
        this.schedule = schedule;
        this.editable = editable;
    }
    
    public Schedule getSchedule() {
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

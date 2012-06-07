package com.cannontech.stars.dr.displayable.model;

import com.cannontech.stars.dr.controlHistory.model.ControlHistory;

public class DisplayableControlHistory {
    
    public enum DisplayableControlHistoryType {
        CONTROLSTATUS,
        DEVICELABEL_CONTROLSTATUS,
    }
    
    private final DisplayableControlHistoryType type;
    private final ControlHistory controlHistory;
    
    public DisplayableControlHistory(final DisplayableControlHistoryType type, final ControlHistory controlHistory) {
        this.type = type;
        this.controlHistory = controlHistory;
    }
    
    public DisplayableControlHistoryType getDisplayType() {
        return type;
    }
    
    public ControlHistory getControlHistory() {
        return controlHistory;
    }
    
    public boolean isControlStatusDisplay() {
        boolean result = type.equals(DisplayableControlHistoryType.CONTROLSTATUS);
        return result;
    }
    
    public boolean isDeviceLabelControlStatusDisplay() {
        boolean result = type.equals(DisplayableControlHistoryType.DEVICELABEL_CONTROLSTATUS);
        return result;
    }
    
}

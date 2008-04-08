package com.cannontech.stars.dr.controlhistory.model;

public enum ControlHistoryStatus {
    NOT_ENROLLED,
    OPTED_OUT,
    CONTROLLED_TODAY,
    CONTROLLED_CURRENT,
    CONTROLLED_NONE;
    
    private static final String keyPrefix = "yukon.dr.program.displayname.controlHistoryStatus.";
    
    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }
    
}

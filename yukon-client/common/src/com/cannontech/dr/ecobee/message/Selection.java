package com.cannontech.dr.ecobee.message;

public class Selection {
    private final String selectionType;
    private final String selectionMatch;
    
    public Selection(String selectionType, String selectionMatch) {
        this.selectionType = selectionType;
        this.selectionMatch = selectionMatch;
    }
    
    public String getSelectionType() {
        return selectionType;
    }
    
    public String getSelectionMatch() {
        return selectionMatch;
    }
}

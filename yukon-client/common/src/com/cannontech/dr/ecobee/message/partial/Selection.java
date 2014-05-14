package com.cannontech.dr.ecobee.message.partial;

/**
 * Describes a selection of Ecobee thermostats.
 */
public class Selection {
    private final SelectionType selectionType;
    private final String selectionMatch;
    
    public static enum SelectionType {
        REGISTERED("registered"), //selects all registered thermostats
        THERMOSTATS("thermostats"), //select individual thermostats by csv string
        MANAGEMENT_SET("managementSet"); //select all thermostats in a management set
        
        private String stringValue;
        private SelectionType(String stringValue) {
            this.stringValue = stringValue;
        }
        
        @Override
        public String toString() {
            return stringValue;
        }
    }
    
    /**
     * @param selectionType The type of selection being used. This determines what values must be present in 
     * selectionMatch.
     * @param selectionMatch Contains the criteria that ecobee uses to determine what thermostats match the selection.
     */
    public Selection(SelectionType selectionType, String selectionMatch) {
        this.selectionType = selectionType;
        this.selectionMatch = selectionMatch;
    }
    
    public String getSelectionType() {
        return selectionType.toString();
    }
    
    public String getSelectionMatch() {
        return selectionMatch;
    }
}

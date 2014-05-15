package com.cannontech.dr.ecobee.message.partial;

import java.util.Collections;

import com.cannontech.common.util.JsonSerializers;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Describes a selection of Ecobee thermostats.
 * 
 * https://www.ecobee.com/home/developer/api/documentation/v1/objects/Selection.shtml
 */
public class Selection {
    private final SelectionType selectionType;
    private final Iterable<String> serialNumbers;
    
    public static enum SelectionType {
        REGISTERED("registered"), //selects all registered thermostats
        THERMOSTATS("thermostats"), //select individual thermostats by csv string
        MANAGEMENT_SET("managementSet"); //select all thermostats in a management set
        
        private String ecobeeString;
        
        private SelectionType(String ecobeeString) {
            this.ecobeeString = ecobeeString;
        }

        public String getEcobeeString() {
            return ecobeeString;
        }
    }
    
    /**
     * @param selectionType The type of selection being used. This determines what values must be present in 
     * selectionMatch.
     * @param selectionMatch Contains the criteria that ecobee uses to determine what thermostats match the selection.
     */
    public Selection(SelectionType selectionType, Iterable<String> serialNumbers) {
        this.selectionType = selectionType;
        this.serialNumbers = serialNumbers;
    }
    
    public Selection(SelectionType selectionType, String serialNumber) {
        this(selectionType, Collections.singleton(serialNumber));
    }
    
    public String getSelectionType() {
        return selectionType.getEcobeeString();
    }

    @JsonSerialize(using=JsonSerializers.Csv.class)
    @JsonGetter("selectionMatch")
    public Iterable<String> getSerialNumbers() {
        return serialNumbers;
    }
}

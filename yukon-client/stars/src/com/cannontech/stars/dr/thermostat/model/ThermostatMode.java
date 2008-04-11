package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.constants.YukonListEntryTypes;

/**
 * Enum which represents thermostat modes
 */
public enum ThermostatMode {
    DEFAULT(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT, "default"), 
    COOL(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL, "cool"), 
    HEAT(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT, "heat"), 
    OFF(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_OFF, "off"), 
    AUTO(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_AUTO, "auto"), 
    EMERGENCY_HEAT(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_EMERGENCY_HEAT,
            "emergencyheat");

    // this key prefix can be found in the following file:
    // com.cannontech.yukon.dr.consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.thermostat.mode.";

    private int definitionId;
    private String commandString;

    private ThermostatMode(int definitionId, String commandString) {
        this.definitionId = definitionId;
        this.commandString = commandString;
    }

    public int getDefinitionId() {
        return definitionId;
    }

    public String getCommandString() {
        return commandString;
    }

    /**
     * Overloaded method to get the enum value for a definitionId
     * @param definitionId - Definition id to get enum for
     * @return Enum value
     */
    public static ThermostatMode valueOf(int definitionId) {

        ThermostatMode[] values = ThermostatMode.values();
        for (ThermostatMode mode : values) {
            int modeDefinitionId = mode.getDefinitionId();
            if (definitionId == modeDefinitionId) {
                return mode;
            }
        }

        throw new IllegalArgumentException("No ThermostatMode found for definitionId: " + definitionId);

    }

    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }
    
    public String getValue(){
        return toString();
    }
}

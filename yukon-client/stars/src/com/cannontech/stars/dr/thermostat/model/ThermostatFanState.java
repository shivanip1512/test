package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.constants.YukonListEntryTypes;

/**
 * Enum which represents thermostat fan modes
 */
public enum ThermostatFanState {
    DEFAULT(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT, "default"), 
    AUTO(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_AUTO, "auto"), 
    ON(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_ON, "on");

    // this key prefix can be found in the following file:
    // com.cannontech.yukon.common.device.bulk.bulkAction.xml
    private final static String keyPrefix = "yukon.dr.consumer.thermostat.fan.";

    private int definitionId;
    private String commandString;

    private ThermostatFanState(int definitionId, String commandString) {
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
    public static ThermostatFanState valueOf(int definitionId) {

        ThermostatFanState[] values = ThermostatFanState.values();
        for (ThermostatFanState fanState : values) {
            int fanStateDefinitionId = fanState.getDefinitionId();
            if (definitionId == fanStateDefinitionId) {
                return fanState;
            }
        }

        throw new IllegalArgumentException("No ThermostatFanState found for definitionId: " + definitionId);

    }

    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }

    public String getValue() {
        return toString();
    }
}

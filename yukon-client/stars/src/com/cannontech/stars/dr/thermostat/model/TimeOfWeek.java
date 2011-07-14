package com.cannontech.stars.dr.thermostat.model;

import com.cannontech.common.constants.YukonListEntryTypes;

/**
 * Enum which represents thermostat schedule time of week
 */
public enum TimeOfWeek {
    WEEKDAY("weekday", YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY),
    MONDAY("mon", YukonListEntryTypes.YUK_DEF_ID_TOW_MONDAY),
    TUESDAY("tue", YukonListEntryTypes.YUK_DEF_ID_TOW_TUESDAY),
    WEDNESDAY("wed", YukonListEntryTypes.YUK_DEF_ID_TOW_WEDNESDAY),
    THURSDAY("thu", YukonListEntryTypes.YUK_DEF_ID_TOW_THURSDAY),
    FRIDAY("fri", YukonListEntryTypes.YUK_DEF_ID_TOW_FRIDAY),
    SATURDAY("sat", YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY),
    SUNDAY("sun", YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY),
    WEEKEND("weekend", YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKEND),
    EVERYDAY("everyday", -1); //for display only purposes
    
    // this key prefix can be found in the following file:
    // com.cannontech.yukon.dr.consumer.xml
    private final static String keyPrefix = "yukon.dr.consumer.thermostat.schedule.";

    private int definitionId;
    private String commandString;

    private TimeOfWeek(String commandString, int definitionId) {
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
    public static TimeOfWeek valueOf(int definitionId) {

        TimeOfWeek[] values = TimeOfWeek.values();
        for (TimeOfWeek timeOfweek : values) {
            int modeDefinitionId = timeOfweek.getDefinitionId();
            if (definitionId == modeDefinitionId) {
                return timeOfweek;
            }
        }

        throw new IllegalArgumentException("No TimeOfWeek found for definitionId: " + definitionId);

    }

    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getDisplayKey() {
        return keyPrefix + name();
    }
    
    /**
     * I18N key for the display text for this action
     * @return Display key
     */
    public String getAbbreviatedDisplayKey() {
        return keyPrefix + name() + "_abbr";
    }
    
    public String getValue(){
        return toString();
    }
}

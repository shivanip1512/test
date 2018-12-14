package com.cannontech.core.roleproperties;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CapControlCommandsAccessLevel implements DisplayableEnum {

    ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS(1), 
    ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS(2), 
    NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS(3),
    NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS(4),
    YUKON_ACTIONS_ONLY(5),
    NONE(6)
    ;
    
    int level;
    CapControlCommandsAccessLevel(int level) {
        this.level = level;
    }
    
    private final static CapControlCommandsAccessLevel[] fieldOperationLevels = 
            new CapControlCommandsAccessLevel[] {ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS};
    private final static CapControlCommandsAccessLevel[] nonOperationLevels = 
            new CapControlCommandsAccessLevel[] {ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS};
    private final static CapControlCommandsAccessLevel[] yukonActionsLevels = 
            new CapControlCommandsAccessLevel[] {ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,YUKON_ACTIONS_ONLY};

    /**
     * Returns true if the user access level is equal to the level specified
     */
    public boolean grantAccess(CapControlCommandsAccessLevel level) {
        return this.level == level.level;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.roleproperty.capControlCommandsAccessLevel." + name();
    }

    public static CapControlCommandsAccessLevel[] getFieldOperationLevels() {
        return fieldOperationLevels;
    }

    public static CapControlCommandsAccessLevel[] getNonOperationLevels() {
        return nonOperationLevels;
    }

    public static CapControlCommandsAccessLevel[] getYukonActionsLevels() {
        return yukonActionsLevels;
    }
}

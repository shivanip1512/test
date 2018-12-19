package com.cannontech.core.roleproperties;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CapControlConfirmationPopups implements DisplayableEnum {

    ALL_COMMANDS(1), 
    OPERATIONAL_COMMANDS(2), 
    NONE(3)
    ;
    
    int level;
    
    CapControlConfirmationPopups(int level) {
        this.level = level;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.roleproperty.CapControlConfirmationPopups." + name();
    }
}

package com.cannontech.web.common.dashboard.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Describes which users can see a dashboard.
 */
public enum Visibility implements  DisplayableEnum {
    SYSTEM, //Built-in, cannot be deleted
    PUBLIC, // Visible to everyone
    PRIVATE, // Only visible to owner
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dashboard.visibility." + name();
    }

}

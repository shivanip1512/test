package com.cannontech.web.common.dashboard.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Describes which users can see a dashboard.
 */
public enum Visibility implements  DisplayableEnum {
    PRIVATE, // Only visible to owner
    PUBLIC, // Visible to everyone
    SYSTEM, //Built-in, cannot be deleted
    ;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dashboard.visibility." + name();
    }

}

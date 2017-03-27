package com.cannontech.web.common.dashboard.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * These are loose categorizations of widgets for organizational purposes. These categories do not affect where widgets
 * can be placed, they are only a convenience for usability. Widgets may be in multiple categories.
 */
public enum WidgetCategory implements DisplayableEnum {
    AMI,
    DR,
    IVVC,
    OTHER
    ;
    
    private static String formatKeyBase = "yukon.web.modules.dashboard.widgetCategory.";
    
    @Override
    public String getFormatKey() {
        return formatKeyBase + name();
    }
}

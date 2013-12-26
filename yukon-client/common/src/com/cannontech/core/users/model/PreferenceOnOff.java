package com.cannontech.core.users.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PreferenceOnOff implements DisplayableEnum {
    ON,
    OFF;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.user.preferences."+ getParentName() +"."+ name();
    }

    public static String getParentName() {
        return "ON_OFF";
    }

}
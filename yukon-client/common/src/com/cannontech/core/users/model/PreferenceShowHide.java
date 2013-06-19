package com.cannontech.core.users.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PreferenceShowHide implements DisplayableEnum {
    SHOW,
    HIDE;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.user.preferences."+ getParentName() +"."+ name();
    }

    public static String getParentName() {
        return "SHOW_HIDE";
    }

}
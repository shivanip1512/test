package com.cannontech.core.users.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum PreferenceTrueFalse implements DisplayableEnum {
    TRUE,
    FALSE;

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.user.preferences."+ getParentName() +"."+ name();
    }

    public static String getParentName() {
        return "TF";
    }

}
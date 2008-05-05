package com.cannontech.web.login;

import com.cannontech.common.i18n.DisplayableEnum;

public enum LoginError implements DisplayableEnum {
    NONE,
    NO_CHANGE,
    NO_PASSWORDMATCH,
    REQUIRED_FIELDS_MISSING,
    INVALID_CREDENTIALS,
    USER_EXISTS,
    PASSWORD_CHANGE_NOT_SUPPORTED;

    private static final String keyPrefix = "yukon.web.login.error.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

}

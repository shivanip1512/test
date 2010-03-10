package com.cannontech.web.login;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ChangeLoginMessage implements DisplayableEnum {
    LOGIN_USERNAME_CHANGED,
    LOGIN_PASSWORD_CHANGED,
    NO_CHANGE,
    NO_USERNAME_CHANGE,
    NO_PASSWORD_CHANGE,
    NO_PASSWORDMATCH,
    REQUIRED_FIELDS_MISSING,
    INVALID_CREDENTIALS_USERNAME_CHANGE,
    INVALID_CREDENTIALS_PASSWORD_CHANGE,
    USER_EXISTS,
    PASSWORD_CHANGE_NOT_SUPPORTED;

    private static final String keyPrefix = "yukon.web.changelogin.message.";
    
    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

}

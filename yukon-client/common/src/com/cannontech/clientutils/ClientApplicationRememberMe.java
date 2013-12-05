package com.cannontech.clientutils;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * Possible values for CLIENT_APPLICATIONS_REMEMBER_ME Global Setting
 */
public enum ClientApplicationRememberMe implements DisplayableEnum {

    NONE,
    USERNAME,
    USERNAME_AND_PASSWORD;

    @Override
    public String getFormatKey() {
        return "yukon.common.clientApplicationRememberMe." + name();
    }
}

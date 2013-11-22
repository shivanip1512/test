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

    /**
     * Returns 'USERNAME' or 'USERNAME_AND_PASSWORD' if the string matches the enum name.
     * Otherwise returns 'NONE'
     */
    public static ClientApplicationRememberMe fromString(String rememberMeSetting) {
        try {
            return ClientApplicationRememberMe.valueOf(rememberMeSetting);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ClientApplicationRememberMe.NONE;
        }
    }
}

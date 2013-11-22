package com.cannontech.clientutils;

import org.apache.commons.lang.StringUtils;

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
        return ClientApplicationRememberMe.valueOf(rememberMeSetting);
    }
}

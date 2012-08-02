package com.cannontech.core.authentication.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum AuthType implements DisplayableEnum {
    PLAIN,
    HASH_SHA,
    HASH_SHA_V2,
    RADIUS,
    NONE,
    AD,
    LDAP;

    @Override
    public String getFormatKey() {
        return "yukon.common.authType." + name();
    }
}

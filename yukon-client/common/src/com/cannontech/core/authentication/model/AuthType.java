package com.cannontech.core.authentication.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * This class represents authentication types stored in the database.  This
 * should generally not be used for presentation--when the user chooses an
 * authentication type, they should be presented with the options in
 * {@link AuthenticationCategory} rather than those in this class.
 */
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

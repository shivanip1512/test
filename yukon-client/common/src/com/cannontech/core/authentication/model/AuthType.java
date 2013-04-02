package com.cannontech.core.authentication.model;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * This class represents authentication types stored in the database.  This
 * should generally not be used for presentation--when the user chooses an
 * authentication type, they should be presented with the options in
 * {@link AuthenticationCategory} rather than those in this class.
 */
public enum AuthType implements DisplayableEnum {
    HASH_SHA,
    HASH_SHA_V2,
    RADIUS,
    NONE,
    AD,
    LDAP,

    /**
     * This is only here for backward compatibility.  Whenever a plain text password is encountered, it should
     * be encrypted immediately.  A process runs in Yukon Service Manager which looks for these and encrypts them.
     */
    PLAIN;

    @Override
    public String getFormatKey() {
        return "yukon.common.authType." + name();
    }
}

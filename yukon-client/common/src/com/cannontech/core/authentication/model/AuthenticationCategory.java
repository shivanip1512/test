package com.cannontech.core.authentication.model;

import java.util.Map;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This enum represents authentication types presented to the user.  These are
 * the valid values of the {@link YukonRoleProperty.DEFAULT_AUTH_TYPE} role
 * property.
 */
public enum AuthenticationCategory implements DisplayableEnum {
    AD(AuthType.AD),
    ENCRYPTED(AuthType.HASH_SHA_V2, AuthType.HASH_SHA),
    LDAP(AuthType.LDAP),
    RADIUS(AuthType.RADIUS),

    /**
     * NONE is used when the login is performed externally and validated
     * in Yukon using the IntegrationLoginController.
     */
    NONE(AuthType.NONE),
    ;

    /**
     * This represents the authentication type which will be used when this
     * type is chosen.  Whenever we add a new, stronger type of encryption,
     * the supportingAuthType of ENCRYPTED should be changed to use this new
     * encryption.
     */
    private final AuthType supportingAuthType;

    /**
     * These represent out-dated authentication types which may have been used
     * in the past when this type of authentication was chosen.
     */
    private final AuthType[] oldAuthTypes;

    private final static Map<AuthType, AuthenticationCategory> byAuthType;

    private AuthenticationCategory(AuthType supportingAuthType, AuthType... oldAuthTypes) {
        this.supportingAuthType = supportingAuthType;
        this.oldAuthTypes = oldAuthTypes;
    }

    static {
        Builder<AuthType, AuthenticationCategory> builder = ImmutableMap.builder();

        for (AuthenticationCategory authenticationCategory : values()) {
            builder.put(authenticationCategory.supportingAuthType, authenticationCategory);
            for (AuthType authType : authenticationCategory.oldAuthTypes) {
                builder.put(authType, authenticationCategory);
            }
        }

        byAuthType = builder.build();
    }

    public AuthType getSupportingAuthType() {
        return supportingAuthType;
    }

    public static AuthenticationCategory getByAuthType(AuthType authType) {
        return byAuthType.get(authType);
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.authenticationCategory." + name();
    }
}

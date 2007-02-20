package com.cannontech.core.authentication.service;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * An interface to be implemented by an AuthenticationProvider
 * that also providers password recovery.
 */
public interface PasswordRecoveryProvider {
    public String getPassword(LiteYukonUser user);
}

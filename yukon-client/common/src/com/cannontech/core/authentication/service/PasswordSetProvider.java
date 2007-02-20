package com.cannontech.core.authentication.service;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * An interface to be implemented by an AuthenticationProvider
 * that also provides the ability to set passwords.
 */
public interface PasswordSetProvider {
    public void setPassword(LiteYukonUser user, String newPassword);
}

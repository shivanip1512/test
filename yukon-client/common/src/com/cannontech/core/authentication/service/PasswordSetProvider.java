package com.cannontech.core.authentication.service;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * An interface to be implemented by an AuthenticationProvider that also provides the ability to set passwords.
 */
public interface PasswordSetProvider {
    public void setPassword(LiteYukonUser user, String newPassword, LiteYukonUser createdBy);
    
    /**
     * This method allows us to compare a supplied password to another password that's been digested.
     * That way we can tell if they are trying to reuse a password.
     */
    public boolean comparePassword(LiteYukonUser yukonUser, String newPassword, String previousDigest);
}

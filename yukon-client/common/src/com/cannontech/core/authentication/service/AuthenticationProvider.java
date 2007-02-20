package com.cannontech.core.authentication.service;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This interface is implemented by an authentication 
 * provider that can be used by Yukon. All providers
 * must be configured in the authContext.xml file
 * to be associated with a key.
 */
public interface AuthenticationProvider {
    /**
     * Determines if the password is valid for the specified user.
     * @param user
     * @param password
     * @return true if the user can be authenticated with the given password
     */
    public boolean login(LiteYukonUser user, String password);
}

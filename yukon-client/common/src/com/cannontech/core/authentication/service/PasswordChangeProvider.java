package com.cannontech.core.authentication.service;

import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * An interface to be implemented by a AuthenticationProvider
 * that also supports password changes. 
 */
public interface PasswordChangeProvider {
    public void changePassword(LiteYukonUser user, String oldPassword, String newPassword) throws BadAuthenticationException;
}

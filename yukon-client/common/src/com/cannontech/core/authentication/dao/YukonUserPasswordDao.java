package com.cannontech.core.authentication.dao;

import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This DAO provides access to the password field in the YukonUser table.
 * Access to this field has been removed from the YukonUser and LiteYukonUser
 * classes to prevent misuse. This class provides RAW access to the stored
 * password. Classes that implement AuthenticationProvider may use this to
 * store a password, a hash of a password, or anything else they choose.
 */
public interface YukonUserPasswordDao {
    /**
     * Checks that the password matches the stored password for the user.
     * @return true if password matches value stored in the password field
     */
    public boolean checkPassword(LiteYukonUser user, String password);

    /**
     * Updates the stored password for the user.
     * @return true if update was successful
     */
    public boolean setPassword(LiteYukonUser user, AuthType authType, String newPassword);
}

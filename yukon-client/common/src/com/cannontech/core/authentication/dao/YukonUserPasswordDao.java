package com.cannontech.core.authentication.dao;

import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This DAO provides access to the password field in the YukonUser table.
 * Access to this field has been removed from the YukonUser and LiteYukonUser
 * classes to prevent misuse. This class provides RAW access to the stored
 * password. Classes that implement AuthenticationProvider may use this to
 * store a password, a hash of a password, or anything else they choose.
 * 
 * As such, THIS DAO IS NOT MEANT FOR GENERAL USE.  Please use
 * {@link AuthenticationService} (which will ultimately call this DAO).
 */
public interface YukonUserPasswordDao {
    /**
     * Gets the digested password for the given user.
     */
    String getDigest(LiteYukonUser user) throws IllegalArgumentException;

    /**
     * Updates the stored digested password for the user.
     * @return true if update was successful
     */
    boolean setPassword(LiteYukonUser user, AuthType authType, String newDigest);

    /**
     * Updates the database, setting the AuthType, LastChangedDate for the user and clearing the password field.
     * This method should ONLY be used for authentication types that don't support passwords.}
     */
    void setAuthType(LiteYukonUser user, AuthType authType);
}

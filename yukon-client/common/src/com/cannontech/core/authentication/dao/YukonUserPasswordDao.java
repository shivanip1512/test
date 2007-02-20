package com.cannontech.core.authentication.dao;

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
     * @param user
     * @param password
     * @return true if password matches value stored in the password field
     */
    public boolean checkPassword(LiteYukonUser user, String password);
    
    /**
     * Updates the stored password for the user.
     * @param user
     * @param newPassword
     * @return true if update was successful
     */
    public boolean changePassword(LiteYukonUser user, String newPassword);
    
    /**
     * Updates the stored password for the user after checking that the oldPassword
     * matches the current value of the stored password.
     * @param user
     * @param oldPassword
     * @param newPassword
     * @return true if update was successful
     */
    public boolean changePassword(LiteYukonUser user, String oldPassword, String newPassword);

    /**
     * Returns the password for the given user. Should only be used
     * in password recovery situations (which aren't recommended).
     * To check a password, use the checkPassword() method.
     * @param user
     * @return stored password for user
     */
    public String recoverPassword(LiteYukonUser user);
}

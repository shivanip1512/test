package com.cannontech.core.authentication.dao;

import java.util.List;

import com.cannontech.core.authentication.model.PasswordHistory;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PasswordHistoryDao {
    void create(PasswordHistory passwordHistory);
    void delete(PasswordHistory passwordHistory);

    PasswordHistory getPasswordHistory(int passwordHistoryId);

    /**
     * Update the AuthType and Password (digest) columns in the database.  This is used when encrypting old
     * plain text passwords but should never be used otherwise.
     */
    void updateWithEncryptedPassword(PasswordHistory passwordHistory);

    /**
     * This method returns all the previous password history entries in the system for the given
     * userId. This can then be used to see if a user is reusing a password before they should be
     * able to. These entries are ordered from the most recent to the oldest.
     */
    List<PasswordHistory> getPasswordHistory(LiteYukonUser user);
}
package com.cannontech.core.authentication.dao;

import java.util.List;

import com.cannontech.core.authentication.model.PasswordHistory;

public interface PasswordHistoryDao {

    public void create(PasswordHistory passwordHistory);
    public void update(PasswordHistory passwordHistory);
    public void delete(PasswordHistory passwordHistory);
    public void delete(int passwordHistoryId);

    /**
     * This method returns all the previous password history entries in the system for the given userId.  This can then be used
     * to see if a user is reusing a password before they should be able to.  These entries are ordered from the most recent to the oldest.
     */
    public List<PasswordHistory> getPasswordHistory(int userId);
    
}
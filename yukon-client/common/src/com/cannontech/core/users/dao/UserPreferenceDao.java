package com.cannontech.core.users.dao;

import java.util.List;

import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserPreferenceDao {

    public void create(UserPreference pref);
    public void update(UserPreference pref);

    /**
     * Find the preference, if any, associated with the given user for this category
     * 
     * @return Returns preference if it exists, else null.
     */
    public UserPreference findPreference(LiteYukonUser user, UserPreferenceName category);

    /**
     * 
     * @return If none exist in the database, then category's default is returned.
     */
    public String getValueOrDefault(LiteYukonUser user, UserPreferenceName category);

    /**
     * Returns a list of all preferences the user has saved.
     */
    public List<UserPreference> findAllSavedPreferencesForUser(LiteYukonUser user);
    
    /**
     * Used to reset the user's preferences to their defaults.
     * 
     * @return Number of preferences deleted (NOT necessarily the same as the # changed!)
     */
    public int deleteAllSavedPreferencesForUser(LiteYukonUser user);
}
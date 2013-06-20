package com.cannontech.core.users.dao;

import java.util.List;

import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserPreferenceDao {

    public void create(UserPreference pref);
    public void update(UserPreference pref);
    
    /**
     * Returns the amount of rows affected, which will be one or zero.
     */
    public int delete(int userPreferenceId);

    /**
     * Find the preference, if any, associated with the given user for this category
     * 
     * @param user
     * @param category
     * @return Returns preference if it exists, else null.
     */
    public UserPreference findPreference(LiteYukonUser user, UserPreferenceName category);

    /**
     * 
     * @param user
     * @param category
     * @return If none exist in the database, then category's default is returned.
     */
    public String getValueOrDefault(LiteYukonUser user, UserPreferenceName category);

    /**
     * Return a list of all preferences the user has saved.
     * 
     * @param user
     * @return
     */
    public List<UserPreference> findAllSavedPreferencesForUser(LiteYukonUser user);
    
    /**
     * Used to reset the user's preferences to their defaults.
     * @param user
     * @return Number of preferences deleted (NOT necessarily the same as the # changed!)
     */
    public int deleteAllSavedPreferencesForUser(LiteYukonUser user);
}
package com.cannontech.core.users.dao;

import java.util.List;

import com.cannontech.core.users.model.YukonUserPreference;
import com.cannontech.core.users.model.YukonUserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonUserPreferenceDao {

    public void create(YukonUserPreference pref);
    public void update(YukonUserPreference pref);
    
    /**
     * Returns the amount of rows affected, which will be one or zero.
     */
    public int delete(int yukonUserPreferenceId);

    /**
     * Find the preference, if any, associated with the given user for this category
     * 
     * @param user
     * @param category
     * @return Returns preference if it exists, else null.
     */
    public YukonUserPreference findPreference(LiteYukonUser user, YukonUserPreferenceName category);

    /**
     * 
     * @param user
     * @param category
     * @return If none exist in the database, then category's default is returned.
     */
    public String getValueOrDefault(LiteYukonUser user, YukonUserPreferenceName category);

    /**
     * Return a list of all preferences the user has saved.
     * 
     * @param user
     * @return
     */
    public List<YukonUserPreference> findAllSavedPreferencesForUser(LiteYukonUser user);
    
    /**
     * Used to reset the user's preferences to their defaults.
     * @param user
     * @return Number of preferences deleted (NOT necessarily the same as the # changed!)
     */
    public int deleteAllSavedPreferencesForUser(LiteYukonUser user);
}
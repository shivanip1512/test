package com.cannontech.core.users.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.core.users.model.PreferenceType;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserPreferenceDao {

    public void create(UserPreference pref);
    public void update(UserPreference pref);

    /**
     * Get the preference, if any, associated with the given user for this category.
     * Tries to find the non-temporary user preference from cache, if not present gets it from DB.
     * 
     * @return Returns preference if it exists, else null.
     */
    public UserPreference getPreference(LiteYukonUser user, UserPreferenceName category);

    /**
     * Used to find User Preferences By Preference Type for a selected user
     * 
     * @param user contains selected user Id
     * @param preferenceType contains type of preferences you would like to find
     * @returns preferences Map for a type if it exists, else null.
     */
    public Map<UserPreferenceName, UserPreference> getUserPreferencesByPreferenceType(LiteYukonUser user,
            PreferenceType preferenceType);

    /**
     * Used to delete User Preferences By Preference Type for a selected user
     * 
     * @param user contains selected user Id
     * @param preferenceType contains type of preference
     * @returns number of preference rows deleted, optional for use
     */
    int deleteUserPreferencesByPreferenceType(LiteYukonUser user, PreferenceType preferenceType);
    
    /**
     * Saves User Preferences to DB from the cache
     */
    public void saveUserPreferenceToDB();
    
    /**
     * Update the list of User preferences to cache/DB
     */
    public void updateUserPreferences(int userID, List<UserPreference> preferences);

}
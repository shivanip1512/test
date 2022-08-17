package com.cannontech.web.user.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.core.users.model.PreferenceGraphVisualTypeOption;
import com.cannontech.core.users.model.PreferencePorterQueueCountsZoomOption;
import com.cannontech.core.users.model.PreferenceTrendZoomOption;
import com.cannontech.core.users.model.PreferenceType;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface UserPreferenceService {

    /**
     * @param user
     * @param preference
     * @param newValue String value of one of the leaf enums, eg. PreferenceShowHide.HIDE > "HIDE"
     */
    public UserPreference savePreference(LiteYukonUser user, UserPreferenceName preference, String newValue);

    /**
     * If there is no saved preference, the default one is returned.
     * 
     * @return String version of the enum value.
     */
    public String getPreference(LiteYukonUser user, UserPreferenceName category);

    /**
     * If the priority is not set or priority doesn't lies between the range (1-14) then return default priority
     */
    Integer getCommanderPriority(LiteYukonUser user);

    public PreferenceGraphVisualTypeOption getDefaultGraphType(LiteYukonUser user);

    public ChartPeriod getDefaultChartPeriod(LiteYukonUser user);
    
    public boolean getDefaultNotificationAlertFlash(LiteYukonUser user);
    
    public boolean getDefaultNotificationAlertSound(LiteYukonUser user);

    public PreferenceGraphVisualTypeOption updatePreferenceGraphType(GraphType type, LiteYukonUser user)
            throws IllegalArgumentException;

    public ChartPeriod updatePreferenceChartPeriod(ChartPeriod period, LiteYukonUser user)
            throws IllegalArgumentException;

    /**
     * If there is no saved preference, the default one which is 3m is returned.
     * 
     * @param user LiteYukonUser
     * @return PreferenceTrendZoomOption enum value.
     */
    public PreferenceTrendZoomOption getDefaultZoomType(LiteYukonUser user);

    /**
     * If there is no saved preference, the default one which is 3m is returned.
     * 
     * @param prefType new Trend Zoom value to be updated
     * @param user LiteYukonUser
     * @return PreferenceTrendZoomOption enum value.
     */   
    public PreferenceTrendZoomOption updatePreferenceZoomType(PreferenceTrendZoomOption prefType, LiteYukonUser user)
            throws IllegalArgumentException;
    
    /**
     * If there is no saved preference, the default one which is 1w is returned.
     * 
     * @param user LiteYukonUser
     * @return PreferencePorterQueueCountsZoomOption enum value.
     */
    public PreferencePorterQueueCountsZoomOption getPorterDefaultZoomType(LiteYukonUser user);

    /**
     * If there is no saved preference, the default one which is 1w is returned.
     * 
     * @param prefType new PorterQueueCounts trend zoom value to be updated
     * @param user LiteYukonUser
     * @return PreferencePorterQueueCountsZoomOption enum value.
     */   
    public PreferencePorterQueueCountsZoomOption updatePorterPreferenceZoomType(PreferencePorterQueueCountsZoomOption prefType, LiteYukonUser user)
            throws IllegalArgumentException;

    
    /**
     * Returns the User preference values for a user by Preference type
     * 
     * @param user , user details
     * @param preferenceType, contains the preference type
     * @returns the Map of preferences for the selected user
     */
    public Map<UserPreferenceName, UserPreference> getUserPreferencesByPreferenceType(LiteYukonUser user,
            PreferenceType preferenceType);

    /**
     * Deletes User Preferences for a selected user By PreferenceType
     * 
     * @param user , user details
     * @param preferenceType, contains the preference type
     * @returns count of rows deleted
     */
    int deleteUserPreferencesByPreferenceType(LiteYukonUser user, PreferenceType preferenceType);

    /**
     * Updates new preferences for a user in cache / DB
     * 
     * @param userId, user details
     * @param preferences, List of user preferences to be set for a user
     */
    void updateUserPreferences(Integer userId, List<UserPreference> preferences);

    /**
     * Return temperature preferences for a user in cache / DB
     * 
     * @param LiteYukonUser
     */
    public boolean getDefaultTemperatureSelection(LiteYukonUser user);

    /**
     * Updates new temperature selection preferences for a user in cache / DB
     * 
     * @param user, user details
     * @param isSelected, boolean value for temperature selection
     */
    public boolean updateTemperatureSelection(LiteYukonUser user, boolean isSelected);

}

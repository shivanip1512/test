package com.cannontech.web.user.service;

import java.util.List;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.core.users.model.PreferenceGraphTimeDurationOption;
import com.cannontech.core.users.model.PreferenceGraphVisualTypeOption;
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

    public int deleteAllSavedPreferencesForUser(LiteYukonUser user);

    /**
     * If there is no saved preference, the default one is returned.
     * 
     * @return String version of the enum value.
     */
    public String getPreference(LiteYukonUser user, UserPreferenceName category);

    /**
     * @param user LiteYukonUser
     * @return Gets only the ones which have been saved in the database.
     */
    public List<UserPreference> findAllSavedPreferencesForUser(LiteYukonUser user);

    public PreferenceGraphVisualTypeOption getDefaultGraphType(LiteYukonUser user);

    public PreferenceGraphTimeDurationOption getDefaultChartPeriod(LiteYukonUser user);

    public PreferenceGraphVisualTypeOption updatePreferenceGraphType(GraphType requestedValueOrNull, LiteYukonUser user)
            throws IllegalArgumentException;

    public PreferenceGraphTimeDurationOption updatePreferenceChartPeriod(ChartPeriod requestedValueOrNull, LiteYukonUser user)
            throws IllegalArgumentException;
}

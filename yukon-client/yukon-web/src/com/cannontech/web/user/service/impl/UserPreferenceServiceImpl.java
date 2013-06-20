package com.cannontech.web.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.core.users.dao.UserPreferenceDao;
import com.cannontech.core.users.model.PreferenceGraphTimeDurationOption;
import com.cannontech.core.users.model.PreferenceGraphVisualTypeOption;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.user.service.UserPreferenceService;

public class UserPreferenceServiceImpl implements UserPreferenceService {

    @Autowired private UserPreferenceDao prefDao;

    @Override
    public UserPreference savePreference(LiteYukonUser user, UserPreferenceName preference, String newValue) {

        UserPreference pref = prefDao.findPreference(user, preference);
        if(pref == null) {
            pref = new UserPreference();
            pref.setName(preference);
            pref.setUserId(user.getUserID());
            pref.setValue(newValue);
            prefDao.create(pref);
        } else {
            pref.setValue(newValue);
            prefDao.update(pref);
        }
        return pref;
    }

    @Override
    public int deleteAllSavedPreferencesForUser(LiteYukonUser user) {
        int count = prefDao.deleteAllSavedPreferencesForUser(user);
        return count;
    }

    @Override
    public String getPreference(LiteYukonUser user, UserPreferenceName category) {
        return prefDao.getValueOrDefault(user, category);
    }

    @Override
    public List<UserPreference> findAllSavedPreferencesForUser(LiteYukonUser user) {
        return prefDao.findAllSavedPreferencesForUser(user);
    }

    @Override
    public PreferenceGraphVisualTypeOption updatePreferenceOrGetDefaultGraphType(GraphType requestedValueOrNull, 
                                                                                 LiteYukonUser user) {
        PreferenceGraphVisualTypeOption prefType = null;
        if (requestedValueOrNull == null) {
            String graphTypeString = this.getPreference(user, UserPreferenceName.GRAPH_DISPLAY_VISUAL_TYPE);
            prefType = PreferenceGraphVisualTypeOption.valueOf(graphTypeString);
        } else {
            prefType = PreferenceGraphVisualTypeOption.fromGraphType(requestedValueOrNull);
            this.savePreference(user, UserPreferenceName.GRAPH_DISPLAY_VISUAL_TYPE, prefType.toString());
        }
        return prefType;
    }

    @Override
    public PreferenceGraphTimeDurationOption updatePreferenceOrGetDefaultChartPeriod(ChartPeriod requestedValueOrNull, 
                                                                                     LiteYukonUser user) {
        PreferenceGraphTimeDurationOption prefType = null;
        if (requestedValueOrNull == null) {
            String chartPeriodString = this.getPreference(user, UserPreferenceName.GRAPH_DISPLAY_TIME_DURATION);
            prefType = PreferenceGraphTimeDurationOption.valueOf(chartPeriodString);
        } else {
            prefType = PreferenceGraphTimeDurationOption.fromChartPeriod(requestedValueOrNull);
            this.savePreference(user, UserPreferenceName.GRAPH_DISPLAY_TIME_DURATION, prefType.toString());
        }
        return prefType;
    }
}

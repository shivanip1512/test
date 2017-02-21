package com.cannontech.web.user.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.users.dao.UserPreferenceDao;
import com.cannontech.core.users.model.PreferenceGraphTimeDurationOption;
import com.cannontech.core.users.model.PreferenceGraphVisualTypeOption;
import com.cannontech.core.users.model.PreferenceTrendZoomOption;
import com.cannontech.core.users.model.PreferenceType;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.user.service.UserPreferenceService;

public class UserPreferenceServiceImpl implements UserPreferenceService {

    @Autowired private UserPreferenceDao prefDao;
    @Autowired @Qualifier("main") private ScheduledExecutor dbUpdateTimer;
    private static final int STARTUP_REF_RATE = 6 * 60 * 60 * 1000; // 6 hours
    private static final int PERIODIC_UPDATE_REF_RATE = 6 * 60 * 60 * 1000; // 6 hours

    @PostConstruct
    public void initialize() {

        Runnable task = () -> {
            saveUserPreferencesToDB();
        };
        dbUpdateTimer.scheduleWithFixedDelay(task, STARTUP_REF_RATE, PERIODIC_UPDATE_REF_RATE, TimeUnit.MILLISECONDS);
    }

    /**
     * Save persist-able/non-editable User Preferences To DB from Cache.
     * This method is called from timer defined above and during server shutdown
     */
    @PreDestroy
    public void saveUserPreferencesToDB() {
       prefDao.saveUserPreferenceToDB();
    }

    @Override
    public Map<UserPreferenceName, UserPreference> getUserPreferencesByPreferenceType(LiteYukonUser user,
            PreferenceType preferenceType) {
        List<UserPreference> prefs = prefDao.getUserPreferencesByPreferenceType(user, preferenceType);
        Map<UserPreferenceName, UserPreference> prefMap = new HashMap<>();
        for (UserPreference preference : prefs) {
            prefMap.put(preference.getName(), preference);
        }
        return prefMap;
    }

    @Override
    public UserPreference savePreference(LiteYukonUser user, UserPreferenceName preferenceName, String newValue) {
        UserPreference preference = new UserPreference(user.getUserID(), preferenceName, newValue, false);
        prefDao.updateUserPreferences(user.getUserID(), Arrays.asList(preference));
        return preference;
    }

    @Override
    public int deleteUserPreferencesByPreferenceType(LiteYukonUser user, PreferenceType preferenceType) {
        int count = prefDao.deleteUserPreferencesByPreferenceType(user, preferenceType);
        return count;
    }

    @Override
    public String getPreference(LiteYukonUser user, UserPreferenceName userPreferenceName) {
        return prefDao.getValueOrDefault(user, userPreferenceName);
    }

    @Override
    public PreferenceGraphVisualTypeOption getDefaultGraphType(LiteYukonUser user) {
        String graphTypeString = this.getPreference(user, UserPreferenceName.GRAPH_DISPLAY_VISUAL_TYPE);
        PreferenceGraphVisualTypeOption prefType = PreferenceGraphVisualTypeOption.valueOf(graphTypeString);
        return prefType;
    }

    @Override
    public ChartPeriod getDefaultChartPeriod(LiteYukonUser user) {
        String chartPeriodString = this.getPreference(user, UserPreferenceName.GRAPH_DISPLAY_TIME_DURATION);
        PreferenceGraphTimeDurationOption prefType = PreferenceGraphTimeDurationOption.valueOf(chartPeriodString);
        return prefType.getChartPeriod();
    }

    @Override
    public boolean getDefaultNotificationAlertFlash(LiteYukonUser user) {
        String notificationAlertFlash = this.getPreference(user, UserPreferenceName.ALERT_FLASH);
        if(notificationAlertFlash.equalsIgnoreCase("on")) {
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public boolean getDefaultNotificationAlertSound(LiteYukonUser user) {
        String notificationAlertSound = this.getPreference(user, UserPreferenceName.ALERT_SOUND);
        if(notificationAlertSound.equalsIgnoreCase("on")) {
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public PreferenceTrendZoomOption getDefaultZoomType(LiteYukonUser user) {
        String graphTypeString = this.getPreference(user, UserPreferenceName.TREND_ZOOM);
        PreferenceTrendZoomOption prefType = PreferenceTrendZoomOption.valueOf(graphTypeString);
        return prefType;
    }

    @Override
    public PreferenceGraphVisualTypeOption updatePreferenceGraphType(GraphType newValue, LiteYukonUser user) {
        if (newValue == null) {
            throw new IllegalArgumentException("Cannot set preference to NULL");
        }
        PreferenceGraphVisualTypeOption prefType = PreferenceGraphVisualTypeOption.fromGraphType(newValue);
        if (prefType == null) {
            throw new IllegalArgumentException("Unknown GraphType ["+ newValue +"]");
        }
        this.savePreference(user, UserPreferenceName.GRAPH_DISPLAY_VISUAL_TYPE, prefType.name());
        return prefType;
    }

    @Override
    public ChartPeriod updatePreferenceChartPeriod(ChartPeriod newValue, LiteYukonUser user) {
        if (newValue == null) {
            throw new IllegalArgumentException("Cannot set preference to NULL");
        }
        PreferenceGraphTimeDurationOption prefType = PreferenceGraphTimeDurationOption.fromChartPeriod(newValue);
        if (prefType == null) {
            throw new IllegalArgumentException("Unknown ChartPeriod ["+ newValue +"]");
        }
        this.savePreference(user, UserPreferenceName.GRAPH_DISPLAY_TIME_DURATION, prefType.name());
        return prefType.getChartPeriod();
    }

    @Override
    public PreferenceTrendZoomOption updatePreferenceZoomType(PreferenceTrendZoomOption prefType, LiteYukonUser user) {
        if (prefType == null) {
            throw new IllegalArgumentException("Unknown ZoomType [" + prefType + "]");
        }
        this.savePreference(user, UserPreferenceName.TREND_ZOOM, prefType.name());
        return prefType;
    }

    @Override
    public void updateUserPreferences(Integer userId, List<UserPreference> preferences) {
        prefDao.updateUserPreferences(userId, preferences);
    }
}

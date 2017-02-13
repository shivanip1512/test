package com.cannontech.web.user.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class UserPreferenceServiceImpl implements UserPreferenceService {

    @Autowired private UserPreferenceDao prefDao;
    @Autowired @Qualifier("main") private ScheduledExecutor dbUpdateTimer;
    private static final int STARTUP_REF_RATE = 6 * 60 * 60 * 1000; // 6 hours
    private static final int PERIODIC_UPDATE_REF_RATE = 6 * 60 * 60 * 1000; // 6 hours

    @PostConstruct
    public void initialize() {

        Runnable task = new Runnable() {
            @Override
            public void run() {
                saveUserPreferencesToDB();
            }
        };

        dbUpdateTimer.scheduleWithFixedDelay(task, STARTUP_REF_RATE, PERIODIC_UPDATE_REF_RATE, TimeUnit.MILLISECONDS);
    }

    /**
     * Save persist-able/non-editable User Preferences To DB from Cache.
     * This method is called from timer defined above and during server shutdown
     */
    @PreDestroy
    public void saveUserPreferencesToDB() {
        ConcurrentMap<Integer, CacheableUserPreferences> userPreferencesCacheMap = userPreferencesCache.asMap();

        userPreferencesCacheMap
            .forEach((userId, categorizedUserReferences) -> {
                Map<String, UserPreference> persistedPreferences = categorizedUserReferences.getPersistedPreferences();
                persistedPreferences.forEach((prefName, pref) -> {
                    if (pref.isUpdated()) {
                        if (pref.getId() != null) {
                            prefDao.update(pref);
                        } else {
                            prefDao.create(pref);
                        }
                        pref.setUpdated(false);
                    }
                });
            });
    }

    private LoadingCache<Integer, CacheableUserPreferences> userPreferencesCache =
        CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<Integer, CacheableUserPreferences>() {
            @Override
            public CacheableUserPreferences load(Integer userId) throws Exception {
                return getUserPreferencesFromDatabase(userId);
            }
        });

    @Override
    public Map<String, UserPreference> findUserPreferencesByPreferenceType(LiteYukonUser user,
            PreferenceType preferenceType) {

        List<UserPreference> prefs = prefDao.findUserPreferencesByPreferenceType(user, preferenceType);
        Map<String, UserPreference> prefMap = new HashMap<>();
        for (UserPreference pp : prefs) {
            prefMap.put(pp.getName().name(), pp);
        }
        return prefMap;
    }

    @Override
    public UserPreference savePreference(LiteYukonUser user, UserPreferenceName preferenceName, String newValue) {
        UserPreference preference = new UserPreference(null, user.getUserID(), preferenceName, newValue, true);
        updateUserPreferences(user.getUserID(), Arrays.asList(preference));
        return preference;
    }

    @Override
    public void updateUserPreferences(Integer userId, List<UserPreference> preferences) {
        for (UserPreference preference : preferences) {
            UserPreferenceName preferenceName = preference.getName();
            if (preferenceName.getPreferenceType() != PreferenceType.EDITABLE) {
                if (userPreferencesCache != null) {
                    synchronized (this) {
                        CacheableUserPreferences cacheableUserPreferences = userPreferencesCache.getIfPresent(userId);
                        if (preferenceName.getPreferenceType() == PreferenceType.NONEDITABLE) {
                            Map<String, UserPreference> persistedPrefs =
                                cacheableUserPreferences.getPersistedPreferences();
                            if (persistedPrefs.containsKey(preferenceName.name())) {
                                String newValue = preference.getValue();
                                // Get old preference, to retain the prefId in cache, instead of directly overwriting
                                preference = persistedPrefs.get(preferenceName.name());
                                preference.setValue(newValue);
                                preference.setUpdated(true);
                            } else {
                                // Add new pref in cache
                                persistedPrefs.put(preferenceName.name(), preference);
                            }
                        } else {
                            Map<String, UserPreference> temporaryPrefs =
                                cacheableUserPreferences.getTemporaryPreferences();
                            if (temporaryPrefs == null) {
                                temporaryPrefs = new HashMap<>();
                                cacheableUserPreferences.setTemporaryPreferences(temporaryPrefs);
                            }
                            // Add or replace the preference
                            temporaryPrefs.put(preferenceName.name(), preference);
                        }
                    }
                }
            } else {
                updateEditablePreferenceToDB(preference, userId);
            }
        }
    }

    /**
     * Updates the Editable preference to DB for a user
     */
    private void updateEditablePreferenceToDB(UserPreference preference, Integer userId) {
        String newValue = preference.getValue();
        // Get preference from DB
        UserPreference oldPreference = prefDao.findPreference(new LiteYukonUser(userId), preference.getName());
        if (oldPreference == null) {
            prefDao.create(preference);
        } else {
            oldPreference.setValue(newValue);
            preference.setId(oldPreference.getId());
            prefDao.update(preference);
        }
    }
    
    @Override
    public int deleteAllSavedPreferencesForUser(LiteYukonUser user) {
        int count = prefDao.deleteAllSavedPreferencesForUser(user);
        return count;
    }

    @Override
    public int deleteUserPreferencesByPreferenceType(LiteYukonUser user, PreferenceType preferenceType) {
        int count = prefDao.deleteUserPreferencesByPreferenceType(user, preferenceType);
        return count;
    }

    @Override
    public String getPreference(LiteYukonUser user, UserPreferenceName userPreferenceName) {
        String value = userPreferenceName.getDefaultValue();
        if (userPreferenceName.getPreferenceType() == PreferenceType.EDITABLE) {
            value = prefDao.getValueOrDefault(user, userPreferenceName);
        } else {
            CacheableUserPreferences cacheableUserPreferences = userPreferencesCache.getUnchecked(user.getUserID());
            if (userPreferenceName.getPreferenceType() == PreferenceType.TEMPORARY) {
                Map<String, UserPreference> tempCache = cacheableUserPreferences.getTemporaryPreferences();
                if (tempCache != null) {
                    value = tempCache.get(userPreferenceName.name()).getValue();
                }
            } else {
                Map<String, UserPreference> persistentCache = cacheableUserPreferences.getPersistedPreferences();
                if (persistentCache.containsKey(userPreferenceName.name())) {
                    value = persistentCache.get(userPreferenceName.name()).getValue();
                }
            }
        }
        return value;
    }

    @Override
    public List<UserPreference> findAllSavedPreferencesForUser(LiteYukonUser user) {
        return prefDao.findAllSavedPreferencesForUser(user);
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

    /**
     * Loads the cache with persisted but non-editable User preferences,
     * temporary preferences are not set here, they will be set as and when commander module is used.
     * 
     * @param userId, user details
     * @returns CacheableUserPreferences with only the preferences which are persisted in DB
     */
    private CacheableUserPreferences getUserPreferencesFromDatabase(Integer userId) {

        Map<String, UserPreference> persistedPrefs = new HashMap<>();

        LiteYukonUser user = new LiteYukonUser(userId);
        List<UserPreference> prefs = prefDao.findUserPreferencesByPreferenceType(user, PreferenceType.NONEDITABLE);
        for (UserPreference pp : prefs) {
            persistedPrefs.put(pp.getName().name(), pp);
        }
        return (new CacheableUserPreferences(null, persistedPrefs));
    }

    @Override
    public Map<UserPreferenceName, UserPreference> findCommanderUserPreferences(LiteYukonUser user) {
        Map<UserPreferenceName, UserPreference> commanderUserPreferences = new HashMap<>();

        CacheableUserPreferences cacheableUserPreferences = userPreferencesCache.getUnchecked(user.getUserID());
        cacheableUserPreferences.getMergedPreferences()
            .forEach((prefName, userPreference) -> {
                if (UserPreferenceName.getCommanderUserPreferenceNames().contains(prefName)) {
                    commanderUserPreferences.put(UserPreferenceName.valueOf(prefName), userPreference);
                }
            });
        return commanderUserPreferences;
    }
}

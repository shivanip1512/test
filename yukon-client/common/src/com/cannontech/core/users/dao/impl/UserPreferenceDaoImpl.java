package com.cannontech.core.users.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;

import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.users.dao.UserPreferenceDao;
import com.cannontech.core.users.model.PreferenceType;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class UserPreferenceDaoImpl implements UserPreferenceDao {

    public final static String TABLE_NAME = "UserPreference";
    public final static String FIELD_PRIMARY_KEY = "PreferenceId";
    public final static String FIELD_NAME = "Name";
    public final static String FIELD_USER_ID = "UserId";
    public final static String FIELD_VALUE = "Value";
    private static final int EXPIRY_TIME = 15;

    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired @Qualifier("main") private ScheduledExecutor dbUpdateTimer;
    private static final int STARTUP_REF_RATE = 6 * 60 * 60 * 1000; // 6 hours
    private static final int PERIODIC_UPDATE_REF_RATE = 6 * 60 * 60 * 1000; // 6 hours
    
    private SimpleTableAccessTemplate<UserPreference> userGroupTemplate;
    
    private LoadingCache<Integer, Map<UserPreferenceName, UserPreference>> userPreferencesCache =
        CacheBuilder.newBuilder().maximumSize(1000)
            .expireAfterAccess(EXPIRY_TIME, TimeUnit.DAYS)
            .build(
                new CacheLoader<Integer, Map<UserPreferenceName, UserPreference>>() {
                    @Override
                    public Map<UserPreferenceName, UserPreference> load(Integer userId) throws Exception {
                        return getUserPreferencesFromDatabase(userId);
                    }
            });

    @PostConstruct
    public void initialize() {
        Runnable task = () -> {
            saveUserPreferenceToDB();
        };
        dbUpdateTimer.scheduleWithFixedDelay(task, STARTUP_REF_RATE, PERIODIC_UPDATE_REF_RATE, TimeUnit.MILLISECONDS);
    }

    /**
     * The DB change Listener updates the cache as per the update/delete
     * DBChange events occurring on the user preferences
     */
    private void createDatabaseChangeListener() {
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDbChangeType() == DbChangeType.UPDATE
                    && dbChange.getDatabase() == DBChangeMsg.CHANGE_USER_PREFERENCE_DB) {
                    UserPreference userPreference = getPreference(dbChange.getId());
                    if (userPreferencesCache != null) {
                        Map<UserPreferenceName, UserPreference> userPrefs =
                            userPreferencesCache.getUnchecked(userPreference.getUserId());
                        // over-write the cache with the DB preference value
                        userPreference.setUpdated(true);
                        userPrefs.put(userPreference.getName(), userPreference);
                    }
                }
                if (dbChange.getDbChangeType() == DbChangeType.DELETE
                    && dbChange.getDatabase() == DBChangeMsg.CHANGE_USER_PREFERENCE_DB_DELETE_BY_USER_ID) {
                    if (userPreferencesCache != null) {
                        Map<UserPreferenceName, UserPreference> userPrefs =
                            userPreferencesCache.getUnchecked(dbChange.getId());
                        List<UserPreferenceName> userPreferenceNames =
                            UserPreferenceName.getUserPreferencesByType(PreferenceType.EDITABLE);
                        for (UserPreferenceName name : userPreferenceNames) {
                            if (userPrefs.containsKey(name)) {
                                userPrefs.remove(name);
                            }
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Loads the cache with persisted but non-editable User preferences,
     * temporary preferences are not set here, they will be set as and when commander module is used.
     * 
     * @param userId, user details
     * @returns User Preferences Map with only the preferences which are persisted in DB
     */
   private Map<UserPreferenceName, UserPreference> getUserPreferencesFromDatabase(Integer userId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreferenceId, UserId, Name, Value");
        sql.append("FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_USER_ID).eq(userId);

        List<UserPreference> preferences = yukonJdbcTemplate.query(sql, new RowMapper());
        Map<UserPreferenceName, UserPreference> persistedPreferenceMap =
                preferences
                    .stream()
                    .collect(Collectors.toMap(UserPreference::getName, Function.identity()));
        return persistedPreferenceMap;
    }
    
    @PostConstruct
    public void init() {
        createDatabaseChangeListener();
        userGroupTemplate = new SimpleTableAccessTemplate<UserPreference>(yukonJdbcTemplate, nextValueHelper);
        userGroupTemplate.setTableName(TABLE_NAME);
        userGroupTemplate.setPrimaryKeyField(FIELD_PRIMARY_KEY);
        userGroupTemplate.setFieldMapper(preferenceFieldMapper);
    }

    private final FieldMapper<UserPreference> preferenceFieldMapper = new FieldMapper<UserPreference>() {
        @Override
        public void extractValues(MapSqlParameterSource p, UserPreference pref) {
            p.addValue(FIELD_USER_ID, pref.getUserId());
            p.addValue(FIELD_NAME, pref.getName());
            p.addValue(FIELD_VALUE, pref.getValue());
        }

        @Override
        public Number getPrimaryKey(UserPreference pref) {
            return pref.getId();
        }

        @Override
        public void setPrimaryKey(UserPreference pref, int newId) {
            pref.setId(newId);
        }
    };

    @Override
    public void create(final UserPreference pref) {
        userGroupTemplate.insert(pref);
    }

    @Override
    public void update(UserPreference pref) {
        userGroupTemplate.update(pref);

        dbChangeManager.processDbChange(pref.getId(), 
                                        DBChangeMsg.CHANGE_USER_PREFERENCE_DB,
                                        DBChangeMsg.CAT_USER_PREFERENCE, 
                                        DBChangeMsg.CAT_USER_PREFERENCE, 
                                        DbChangeType.UPDATE);
    }

    /**
     * Get User preference from database by user Id and preference name, if not found return the default value
     */
    private UserPreference getPreferenceFromDbOrDefault(LiteYukonUser user, UserPreferenceName prefName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreferenceId, UserId, Name, Value");
        sql.append("FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_USER_ID).eq(user.getUserID());
        sql.append("AND " + FIELD_NAME).eq(prefName);

        try {
            return yukonJdbcTemplate.queryForObject(sql, new RowMapper());
        } catch (EmptyResultDataAccessException e) {
            UserPreference userPreference = new UserPreference(); // use null id for "new"
            userPreference.setUserId(user.getUserID());
            userPreference.setName(prefName);
            userPreference.setValue(prefName.getDefaultValue());
            userPreference.setUpdated(false); // need to archive
            return userPreference;
        }
    }
    
    /**
     * Get User preference from database by preference Id
     * 
     * @throws EmptyResultDataAccessException if the preference is not found
     */
    private UserPreference getPreference(int preferenceId) throws EmptyResultDataAccessException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreferenceId, UserId, Name, Value");
        sql.append("FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_PRIMARY_KEY).eq(preferenceId);

        UserPreference pref = yukonJdbcTemplate.queryForObject(sql, new RowMapper());
        return pref;
    }

    @Override
    public UserPreference getPreference(LiteYukonUser user, UserPreferenceName userPreferenceName) {

        if (userPreferenceName == null) {
            throw new IllegalArgumentException("UserPreferenceDaoImpl cannot get Preference for null UserPreference.");
        }

        UserPreference value = null;
        Map<UserPreferenceName, UserPreference> cacheableUserPreferences =
            userPreferencesCache.getUnchecked(user.getUserID());

        if (cacheableUserPreferences.containsKey(userPreferenceName)) {
            value = cacheableUserPreferences.get(userPreferenceName);
        } else if (userPreferenceName.getPreferenceType() != PreferenceType.TEMPORARY) {
            value = getPreferenceFromDbOrDefault(user, userPreferenceName);
            cacheableUserPreferences.put(userPreferenceName, value);
        }
        return value;
    }

    private static class RowMapper implements YukonRowMapper<UserPreference> {
        @Override
        public UserPreference mapRow(YukonResultSet rs) throws SQLException {
            UserPreference pref = new UserPreference();
            UserPreferenceName pp = rs.getEnum(FIELD_NAME, UserPreferenceName.class);

            pref.setId(rs.getInt(FIELD_PRIMARY_KEY));
            pref.setUserId(rs.getInt(FIELD_USER_ID));
            pref.setName(pp);
            pref.setValue(rs.getStringSafe(FIELD_VALUE));

            return pref;
        }
    }

    @Override
    public Map<UserPreferenceName, UserPreference> getUserPreferencesByPreferenceType(LiteYukonUser user,
            PreferenceType preferenceType) {
        Map<UserPreferenceName, UserPreference> userPreferences = new HashMap<>();
        Map<UserPreferenceName, UserPreference> cacheableUserPreferences =
            userPreferencesCache.getUnchecked(user.getUserID());

        List<UserPreferenceName> userPreferenceNames = UserPreferenceName.getUserPreferencesByType(preferenceType);

        for (UserPreferenceName userPreferenceName : userPreferenceNames) {
            if (cacheableUserPreferences.containsKey(userPreferenceName)) {
                UserPreference userPreference = cacheableUserPreferences.get(userPreferenceName);
                userPreferences.put(userPreferenceName, userPreference);
            }
        }
        return userPreferences;
    }

    @Override
    public int deleteUserPreferencesByPreferenceType(LiteYukonUser user, PreferenceType preferenceType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_USER_ID).eq(user.getUserID());
        sql.append("AND " + FIELD_NAME).in(UserPreferenceName.getUserPreferencesByType(preferenceType));

        int rowsDeleted = yukonJdbcTemplate.update(sql);

        dbChangeManager.processDbChange(user.getUserID(), 
                                        DBChangeMsg.CHANGE_USER_PREFERENCE_DB_DELETE_BY_USER_ID,
                                        DBChangeMsg.CAT_USER_PREFERENCE, 
                                        DBChangeMsg.CAT_USER_PREFERENCE, 
                                        DbChangeType.DELETE);
        return rowsDeleted;
    }
    
    /**
     * Save persist-able/non-editable User Preferences To DB from Cache.
     * This method is called from timer defined above and during server shutdown
     */
    @PreDestroy
    @Override
    public void saveUserPreferenceToDB() {
        ConcurrentMap<Integer, Map<UserPreferenceName, UserPreference>> allUserPreferences =
            userPreferencesCache.asMap();

        allUserPreferences.forEach((userId, userPrefMap) -> {
            userPrefMap.forEach((prefName, pref) -> {
                if (!pref.isUpdated() && pref.getName().getPreferenceType() == PreferenceType.NONEDITABLE) {
                    if (pref.getId() != null) {
                        update(pref);
                    } else {
                        create(pref);
                    }
                    pref.setUpdated(true); // Updated to DB
                }
            });
        });
    }
    
    @Override
    public void updateUserPreferences(int userID, List<UserPreference> preferences) {
        boolean isValueChanged = false;
        for (UserPreference preference : preferences) {
            UserPreferenceName preferenceName = preference.getName();
            if (userPreferencesCache != null) {
                Map<UserPreferenceName, UserPreference> cacheableUserPreferences =
                    userPreferencesCache.getIfPresent(userID);
                if (CollectionUtils.isEmpty(cacheableUserPreferences)) {
                    cacheableUserPreferences = new HashMap<>();
                    userPreferencesCache.put(userID, cacheableUserPreferences);
                }
                if (cacheableUserPreferences.containsKey(preferenceName)) {
                    String newValue = preference.getValue();
                    // Get old preference, to retain the prefId in cache, instead of directly overwriting
                    preference = cacheableUserPreferences.get(preferenceName);
                    if (!newValue.equalsIgnoreCase(preference.getValue())) {
                        isValueChanged = true; // old and new values don't match
                    }
                    preference.setValue(newValue);
                } else {
                    // Add new pref in cache
                    cacheableUserPreferences.put(preferenceName, preference);
                }
                preference.setUpdated(false); // Not updated to DB
            }
            if (preferenceName.getPreferenceType() == PreferenceType.EDITABLE) {
                preference.setUpdated(true); // Updated to DB
                if (preference.getId() != null && isValueChanged) {
                    update(preference);
                } else if (preference.getId() == null) {
                    create(preference);
                }
            }
        }
    }
}
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;

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

    /**
     * The DB change Listener updates the cache as per the update/delete
     * dbchange events occurring on the user preferences
     */
    private void createDatabaseChangeListener() {
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {
            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                if (dbChange.getDbChangeType() == DbChangeType.UPDATE
                    && dbChange.getDatabase() == DBChangeMsg.CHANGE_USER_PREFERENCE_DB) {
                    UserPreference userPreference = getPreference(dbChange.getId());
                    if (userPreferencesCache != null) {
                        synchronized (this) {
                            Map<UserPreferenceName, UserPreference> userPrefs =
                                userPreferencesCache.getUnchecked(userPreference.getUserId());
                            // over-write the cache with the DB preference value
                            userPreference.setUpdated(true);
                            userPrefs.put(userPreference.getName(), userPreference);
                        }
                    }
                }
                if (dbChange.getDbChangeType() == DbChangeType.DELETE
                    && dbChange.getDatabase() == DBChangeMsg.CHANGE_USER_PREFERENCE_DB_DELETE_BY_USER_ID) {
                    if (userPreferencesCache != null) {
                        synchronized (this) {
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

        LiteYukonUser user = new LiteYukonUser(userId);
        List<UserPreference> preferences = getUserPreferencesByPreferenceType(user, PreferenceType.EDITABLE);
        preferences.addAll(getUserPreferencesByPreferenceType(user, PreferenceType.NONEDITABLE));
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
     * Get User preference from database by user Id and preference name
     * @throws EmptyResultDataAccessException if the preference is not found
     */
    private UserPreference getPreference(LiteYukonUser user, UserPreferenceName prefName)
            throws EmptyResultDataAccessException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreferenceId, UserId, Name, Value");
        sql.append("FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_USER_ID).eq(user.getUserID());
        sql.append("AND " + FIELD_NAME).eq(prefName);

        UserPreference pref = yukonJdbcTemplate.queryForObject(sql, new RowMapper());
        return pref;
    }
    
    /**
     * Get User preference from database by preference Id
     * @throws EmptyResultDataAccessException if the preference is not found
     */
    private UserPreference getPreference(int preferenceId)
            throws EmptyResultDataAccessException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreferenceId, UserId, Name, Value");
        sql.append("FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_PRIMARY_KEY).eq(preferenceId);

        UserPreference pref = yukonJdbcTemplate.queryForObject(sql, new RowMapper());
        return pref;
    }

    @Override
    public UserPreference findPreference(LiteYukonUser user, UserPreferenceName userPreferenceName) {
        UserPreference value = null;
        try {
            Map<UserPreferenceName, UserPreference> cacheableUserPreferences =
                userPreferencesCache.getUnchecked(user.getUserID());

            if (cacheableUserPreferences.containsKey(userPreferenceName)) {
                value = cacheableUserPreferences.get(userPreferenceName);
            } else if (userPreferenceName.getPreferenceType() != PreferenceType.TEMPORARY) {
                value = getPreference(user, userPreferenceName);
                cacheableUserPreferences.put(userPreferenceName, value);
            }
        } catch (EmptyResultDataAccessException e) {/* return nulls for find methods */}

        return value;
    }

    @Override
    public String getValueOrDefault(LiteYukonUser user, UserPreferenceName prefName) {
        if (prefName == null) {
            throw new IllegalArgumentException("UserPreferenceDaoImpl cannot get Preference for null UserPreference.");
        }
        if (user == null) {
            return prefName.getDefaultValue();
        }
        UserPreference value = findPreference(user, prefName);
        if (value != null) {
            return value.getValue();
        }
        return prefName.getDefaultValue();
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
    public List<UserPreference> getUserPreferencesByPreferenceType(LiteYukonUser user, PreferenceType preferenceType) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreferenceId, UserId, Name, Value");
        sql.append("FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_USER_ID).eq(user.getUserID());
        sql.append("AND " + FIELD_NAME).in(UserPreferenceName.getUserPreferencesByType(preferenceType));

        List<UserPreference> prefs = yukonJdbcTemplate.query(sql, new RowMapper());
        return prefs;
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
                synchronized (this) {
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
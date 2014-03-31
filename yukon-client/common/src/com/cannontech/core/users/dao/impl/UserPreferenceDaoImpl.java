package com.cannontech.core.users.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.users.dao.UserPreferenceDao;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class UserPreferenceDaoImpl implements UserPreferenceDao {

    public final static String TABLE_NAME = "UserPreference";
    public final static String FIELD_PRIMARY_KEY = "PreferenceId";
    public final static String FIELD_NAME = "Name";
    public final static String FIELD_USER_ID = "UserId";
    public final static String FIELD_VALUE = "Value";

    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    private SimpleTableAccessTemplate<UserPreference> userGroupTemplate;

    @PostConstruct
    public void init() {
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

    @Override
    public UserPreference findPreference(LiteYukonUser user, UserPreferenceName prefName) {
        try {
            return getPreference(user, prefName);
        } catch (EmptyResultDataAccessException e) {/* return nulls for find methods */}

        return null;
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

    @Override
    public List<UserPreference> findAllSavedPreferencesForUser(LiteYukonUser user) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreferenceId, UserId, Name, Value");
        sql.append("FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_USER_ID).eq(user.getUserID());

        List<UserPreference> prefs = yukonJdbcTemplate.query(sql, new RowMapper());
        return prefs;
    }

    @Override
    public int deleteAllSavedPreferencesForUser(LiteYukonUser user) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM "+ TABLE_NAME);
        sql.append("WHERE " + FIELD_USER_ID).eq(user.getUserID());

        int rowsDeleted = yukonJdbcTemplate.update(sql);

        dbChangeManager.processDbChange(user.getUserID(), 
                                        DBChangeMsg.CHANGE_USER_PREFERENCE_DB_DELETE_BY_USER_ID,
                                        DBChangeMsg.CAT_USER_PREFERENCE, 
                                        DBChangeMsg.CAT_USER_PREFERENCE, 
                                        DbChangeType.DELETE);
        return rowsDeleted;
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

}
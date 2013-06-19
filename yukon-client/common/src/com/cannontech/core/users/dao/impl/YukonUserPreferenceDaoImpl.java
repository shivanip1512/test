package com.cannontech.core.users.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.users.dao.YukonUserPreferenceDao;
import com.cannontech.core.users.model.YukonUserPreference;
import com.cannontech.core.users.model.YukonUserPreferenceName;
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

public class YukonUserPreferenceDaoImpl implements YukonUserPreferenceDao {

    public final static String TABLE_NAME = "YukonUserPreference";
    public final static String FIELD_PRIMARY_KEY = "PreferenceId";
    public final static String FIELD_NAME = "Name";
    public final static String FIELD_USER_ID = "UserId";
    public final static String FIELD_VALUE = "Value";

    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    private SimpleTableAccessTemplate<YukonUserPreference> userGroupTemplate;

    @PostConstruct
    public void init() {
        userGroupTemplate = new SimpleTableAccessTemplate<YukonUserPreference>(yukonJdbcTemplate, nextValueHelper);
        userGroupTemplate.setTableName(TABLE_NAME);
        userGroupTemplate.setPrimaryKeyField(FIELD_PRIMARY_KEY);
        userGroupTemplate.setFieldMapper(preferenceFieldMapper);
    }

    private final FieldMapper<YukonUserPreference> preferenceFieldMapper = new FieldMapper<YukonUserPreference>() {
        @Override
        public void extractValues(MapSqlParameterSource p, YukonUserPreference pref) {
            p.addValue(FIELD_USER_ID, pref.getUserId());
            p.addValue(FIELD_NAME, pref.getName());
            p.addValue(FIELD_VALUE, pref.getValue());
        }

        @Override
        public Number getPrimaryKey(YukonUserPreference pref) {
            return pref.getId();
        }

        @Override
        public void setPrimaryKey(YukonUserPreference pref, int newId) {
            pref.setId(newId);
        }
    };

    @Override
    public void create(final YukonUserPreference pref) {
        userGroupTemplate.insert(pref);
    }

    @Override
    public void update(YukonUserPreference pref) {
        userGroupTemplate.update(pref);

        dbChangeManager.processDbChange(pref.getId(), 
                                        DBChangeMsg.CHANGE_USER_PREFERENCE_DB,
                                        DBChangeMsg.CAT_YUKON_USER_PREFERENCE, 
                                        DBChangeMsg.CAT_YUKON_USER_PREFERENCE, 
                                        DbChangeType.UPDATE);
    }

    @Override
    public int delete(int yukonUserPreferenceId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM "+ TABLE_NAME);
        sql.append("WHERE " + FIELD_PRIMARY_KEY).eq(yukonUserPreferenceId);

        int affected = yukonJdbcTemplate.update(sql);

        dbChangeManager.processDbChange(yukonUserPreferenceId, 
                                        DBChangeMsg.CHANGE_USER_PREFERENCE_DB,
                                        DBChangeMsg.CAT_YUKON_USER_PREFERENCE, 
                                        DBChangeMsg.CAT_YUKON_USER_PREFERENCE, 
                                        DbChangeType.DELETE);

        return affected;
    }

    private YukonUserPreference getPreference(LiteYukonUser user, YukonUserPreferenceName prefName) throws EmptyResultDataAccessException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PreferenceId, UserId, Name, Value");
        sql.append("FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_USER_ID).eq(user.getUserID());
        sql.append("AND " + FIELD_NAME).eq(prefName);

        YukonUserPreference pref = yukonJdbcTemplate.queryForObject(sql, new YukonUserPreferenceRowMapper());
        return pref;
    }

    @Override
    public YukonUserPreference findPreference(LiteYukonUser user, YukonUserPreferenceName prefName) {
        try {
            return getPreference(user, prefName);
        } catch (EmptyResultDataAccessException e) {/* return nulls for find methods */}

        return null;
    }

    @Override
    public String getValueOrDefault(LiteYukonUser user, YukonUserPreferenceName prefName) {
        if (prefName == null) {
            throw new IllegalArgumentException("YukonUserPreferenceDaoImpl cannot get Preference for null YukonUserPreference.");
        }
        if (user == null) {
            return prefName.getDefaultValue();
        }
        YukonUserPreference value = findPreference(user, prefName);
        if (value != null) {
            return value.getValue();
        }
        return prefName.getDefaultValue();
    }

    @Override
    public List<YukonUserPreference> findAllSavedPreferencesForUser(LiteYukonUser user) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM " + TABLE_NAME);
        sql.append("WHERE " + FIELD_USER_ID).eq(user.getUserID());

        List<YukonUserPreference> prefs = yukonJdbcTemplate.query(sql, new YukonUserPreferenceRowMapper());
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
                                        DBChangeMsg.CAT_YUKON_USER_PREFERENCE, 
                                        DBChangeMsg.CAT_YUKON_USER_PREFERENCE, 
                                        DbChangeType.DELETE);
        return rowsDeleted;
    }

    public static class YukonUserPreferenceRowMapper implements YukonRowMapper<YukonUserPreference> {
        @Override
        public YukonUserPreference mapRow(YukonResultSet rs) throws SQLException {
            YukonUserPreference pref = new YukonUserPreference();
            YukonUserPreferenceName pp = YukonUserPreferenceName.valueOf(rs.getString(FIELD_NAME));

            pref.setId(rs.getInt(FIELD_PRIMARY_KEY));
            pref.setUserId(rs.getInt(FIELD_USER_ID));
            pref.setName(pp);
            pref.setValue(rs.getStringSafe(FIELD_VALUE));

            return pref;
        }
    }

}
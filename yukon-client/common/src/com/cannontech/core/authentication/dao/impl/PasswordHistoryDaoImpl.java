package com.cannontech.core.authentication.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.dao.PasswordHistoryDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.PasswordHistory;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;

public class PasswordHistoryDaoImpl implements PasswordHistoryDao, InitializingBean { 
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    
    private SimpleTableAccessTemplate<PasswordHistory> passwordHistoryTemplate;
    
    private FieldMapper<PasswordHistory> passwordHistoryFieldMapper = new FieldMapper<PasswordHistory>() {
        @Override
        public void extractValues(MapSqlParameterSource p, PasswordHistory passwordHistory) {
            p.addValue("UserId", passwordHistory.getUserId());
            p.addValue("Password", passwordHistory.getPassword());
            p.addValue("AuthType", passwordHistory.getAuthType());
            p.addValue("PasswordChangedDate", passwordHistory.getPasswordChangedDate());
        }
        
        @Override
        public Number getPrimaryKey(PasswordHistory passwordHistory) {
            return passwordHistory.getPasswordHistoryId();
        }
        
        @Override
        public void setPrimaryKey(PasswordHistory passwordHistory, int newId) {
            passwordHistory.setPasswordHistoryId(newId);
        }
    };
    
    @Override
    public void afterPropertiesSet() throws Exception {
        passwordHistoryTemplate = new SimpleTableAccessTemplate<PasswordHistory>(yukonJdbcTemplate, nextValueHelper);
        passwordHistoryTemplate.setTableName("PasswordHistory");
        passwordHistoryTemplate.setPrimaryKeyField("PasswordHistoryId");
        passwordHistoryTemplate.setFieldMapper(passwordHistoryFieldMapper);
        passwordHistoryTemplate.setPrimaryKeyValidOver(0);
    }

    private final static YukonRowMapper<PasswordHistory> passwordHistoryRowMapper = new YukonRowMapper<PasswordHistory>() {
        @Override
        public PasswordHistory mapRow(YukonResultSet rs) throws SQLException {
            PasswordHistory passwordHistory = new PasswordHistory();
            
            passwordHistory.setPasswordHistoryId(rs.getInt("PasswordHistoryId"));
            passwordHistory.setUserId(rs.getInt("UserId"));
            passwordHistory.setPassword(rs.getStringSafe("Password"));
            passwordHistory.setAuthType(rs.getEnum("AuthType", AuthType.class));
            passwordHistory.setPasswordChangedDate(rs.getInstant("PasswordChangedDate"));
            
            return passwordHistory;
        }
    };

    @Override
    public void create(final PasswordHistory passwordHistory) {
        passwordHistoryTemplate.save(passwordHistory);
    }

    @Override
    public void delete(PasswordHistory passwordHistory) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM PasswordHistory");
        sql.append("WHERE PasswordHistoryId").eq(passwordHistory.getPasswordHistoryId());
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public PasswordHistory getPasswordHistory(int passwordHistoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PasswordHistoryId, UserId, Password, AuthType, PasswordChangedDate");
        sql.append("FROM PasswordHistory WHERE PasswordHistoryId").eq(passwordHistoryId);
        PasswordHistory passwordHistory = yukonJdbcTemplate.queryForObject(sql, passwordHistoryRowMapper);
        return passwordHistory;
    }

    @Override
    public void updateWithEncryptedPassword(PasswordHistory passwordHistory) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        updateSql.append("UPDATE PasswordHistory");
        updateSql.set("AuthType", passwordHistory.getAuthType(), "Password", passwordHistory.getPassword());
        updateSql.append("WHERE PasswordHistoryId").eq(passwordHistory.getPasswordHistoryId());
        yukonJdbcTemplate.update(updateSql);
    }

    @Override
    public List<PasswordHistory> getPasswordHistory(LiteYukonUser user) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PasswordHistoryId, UserId, Password, AuthType, PasswordChangedDate");
        sql.append("FROM PasswordHistory");
        sql.append("WHERE UserId").eq(user.getUserID());
        sql.append("ORDER BY PasswordChangedDate DESC");

        List<PasswordHistory> passwordHistories = yukonJdbcTemplate.query(sql, passwordHistoryRowMapper);
        return passwordHistories;
    }
}
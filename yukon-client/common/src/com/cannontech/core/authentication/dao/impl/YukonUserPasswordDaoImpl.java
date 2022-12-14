package com.cannontech.core.authentication.dao.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.dao.PasswordHistoryDao;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.PasswordHistory;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserPasswordDaoImpl implements YukonUserPasswordDao {
    @Autowired private PasswordHistoryDao passwordHistoryDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public String getDigest(LiteYukonUser user) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Password FROM YukonUser WHERE UserId").eq(user.getUserID());

        String digest = jdbcTemplate.queryForString(sql);
        return digest;
    }

    @Override
    public boolean setPassword(LiteYukonUser user, AuthType authType, String newDigest) {
        Instant now = Instant.now();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser").set("Password", newDigest, "AuthType", authType, "LastChangedDate", now,
                "ForceReset", YNBoolean.NO);
        sql.append("WHERE UserId").eq(user.getUserID());
        boolean passwordUpdated = jdbcTemplate.update(sql) == 1;

        PasswordHistory passwordHistory = new PasswordHistory(user.getUserID(), newDigest,  authType, now);
        passwordHistoryDao.create(passwordHistory);

        return passwordUpdated;
    }

    @Override
    public void setPasswordWithoutHistory(LiteYukonUser user, AuthType authType, String newDigest) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser").set("Password", newDigest, "AuthType", authType);
        sql.append("WHERE UserId").eq(user.getUserID());
        jdbcTemplate.update(sql);
    }

    @Override
    public void setAuthType(LiteYukonUser user, AuthType authType) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser").set("Password", " ", "AuthType", authType, "LastChangedDate", new Instant(),
                "ForceReset", YNBoolean.NO);
        sql.append("WHERE UserId").eq(user.getUserID());
        jdbcTemplate.update(sql);
    }

    @Override
    public void setForceResetForUser(LiteYukonUser user, YNBoolean forceReset) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser").set("ForceReset", forceReset);
        sql.append("WHERE UserId").eq(user.getUserID());
        jdbcTemplate.update(sql);
    }
}

package com.cannontech.core.authentication.dao.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.dao.PasswordHistoryDao;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.PasswordHistory;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserPasswordDaoImpl implements YukonUserPasswordDao {
    @Autowired private PasswordHistoryDao passwordHistoryDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonUserDao userDao;

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
        sql.append("UPDATE YukonUser").set("Password", newDigest, "AuthType", authType, "LastChangedDate", now);
        sql.append("WHERE UserId").eq(user.getUserID());
        boolean passwordUpdated = jdbcTemplate.update(sql) == 1;
     
        PasswordHistory passwordHistory = new PasswordHistory(user.getUserID(), newDigest,  authType, now);
        passwordHistoryDao.create(passwordHistory);
        
        if (user.isForceReset()) {
            user.setForceReset(false);
            userDao.update(user);
        }
     
        return passwordUpdated;
    }
}

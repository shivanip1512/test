package com.cannontech.core.authentication.dao.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.dao.PasswordHistoryDao;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.PasswordHistory;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserPasswordDaoImpl implements YukonUserPasswordDao {
    @Autowired private PasswordHistoryDao passwordHistoryDao;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private YukonUserDao userDao;

    @Override
    public boolean checkPassword(LiteYukonUser user, String password) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Password FROM YukonUser WHERE UserId").eq(user.getUserID());
        try {
            String pwd = jdbcTemplate.queryForString(sql);
            return pwd.equals(password);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean setPassword(LiteYukonUser user, AuthType authType, String newPassword) {
        Instant now = Instant.now();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser").set("Password", newPassword, "AuthType", authType, "LastChangedDate", now);
        sql.append("WHERE UserId").eq(user.getUserID());
        boolean passwordUpdated = jdbcTemplate.update(sql) == 1;
     
        PasswordHistory passwordHistory = new PasswordHistory(user.getUserID(), newPassword,  authType, now);
        passwordHistoryDao.create(passwordHistory);
        
        if (user.isForceReset()) {
            user.setForceReset(false);
            userDao.update(user);
        }
     
        return passwordUpdated;
    }
}

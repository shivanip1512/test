package com.cannontech.core.authentication.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;

public class YukonUserPasswordDaoImpl implements YukonUserPasswordDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

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
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser").set("Password", newPassword, "AuthType", authType);
        sql.append("WHERE UserId").eq(user.getUserID());
        return jdbcTemplate.update(sql) == 1;
    }
}

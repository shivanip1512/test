package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.database.data.lite.LiteYukonUser;

public final class LiteYukonUserMapper implements
        ParameterizedRowMapper<LiteYukonUser> {
    @Override
    public LiteYukonUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        LiteYukonUser user = new LiteYukonUser();
        user.setUserID(rs.getInt("UserID"));
        user.setUsername(rs.getString("UserName"));
        user.setAuthType(AuthType.valueOf(rs.getString("AuthType")));
        user.setStatus(rs.getString("Status").intern());
        return user;
    }
}
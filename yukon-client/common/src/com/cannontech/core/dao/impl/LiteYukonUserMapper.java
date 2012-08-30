package com.cannontech.core.dao.impl;

import java.sql.SQLException;

import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;

public final class LiteYukonUserMapper implements YukonRowMapper<LiteYukonUser> {
    @Override
    public LiteYukonUser mapRow(YukonResultSet rs) throws SQLException {
        LiteYukonUser user = new LiteYukonUser();
        user.setUserID(rs.getInt("UserID"));
        user.setUsername(rs.getString("UserName"));
        user.setAuthType(AuthType.valueOf(rs.getString("AuthType")));
        user.setLoginStatus(LoginStatusEnum.retrieveLoginStatus(rs.getString("Status")));
        user.setLastChangedDate(rs.getInstant("LastChangedDate"));
        user.setForceReset(rs.getEnum("ForceReset", YNBoolean.class).getBoolean());
        user.setUserGroupId(rs.getNullableInt("UserGroupId"));
        return user;
    }
}
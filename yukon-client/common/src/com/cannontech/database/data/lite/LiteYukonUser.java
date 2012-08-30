package com.cannontech.database.data.lite;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.Instant;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;

public class LiteYukonUser extends LiteBase {
    private String username;
    private LoginStatusEnum loginStatus;
    private AuthType authType;
    private Instant lastChangedDate;
    private boolean forceReset;
    private Integer userGroupId;

    public LiteYukonUser() {
        this(0,null,null);
    }

    public LiteYukonUser(int id) {
        this(id,null,null);
    }

    public LiteYukonUser(int id, String username) {
        this(id, username, null);
    }

    public LiteYukonUser(int id, String username, LoginStatusEnum loginStatus) {
        this(id, username, loginStatus, AuthType.PLAIN, Instant.now(), false, null);
    }

    public LiteYukonUser(int id, String username, LoginStatusEnum loginStatus, AuthType authType, Instant lastChangedDate,
                         boolean forceReset, Integer userGroupId) {
        setLiteType(LiteTypes.YUKON_USER);
        setUserID(id);
        setUsername(username);
        setLoginStatus(loginStatus);
        setAuthType(authType);
        setLastChangedDate(lastChangedDate);
        setForceReset(forceReset);
        setUserGroupId(userGroupId);
    }

    public void retrieve( String dbAlias ) {
        
        YukonJdbcTemplate yukonJdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Username, Status, AuthType, LastChangedDate, ForceReset, UserGroupId");
        sql.append("FROM YukonUser");
        sql.append("WHERE  UserId").eq(getUserID());

        yukonJdbcTemplate.query(sql, new ResultSetExtractor<LiteYukonUser>() {
            public LiteYukonUser extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                setUsername(rs.getString("Username").trim() );
                setLoginStatus(LoginStatusEnum.retrieveLoginStatus(rs.getString("Status")));
                setAuthType(AuthType.valueOf(rs.getString("AuthType")));
                setLastChangedDate(new Instant(rs.getDate("lastChangedDate")));
                setForceReset("Y".equals(rs.getString("ForceReset")));
                setUserGroupId(rs.getInt("UserGroupId"));
                return null;
            }
        });

    }

    @Override
    public String toString(){
        return getUsername();
    }
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserID() {
        return getLiteID();
    }
    public void setUserID(int userID) {
        setLiteID(userID);
    }

    public LoginStatusEnum getLoginStatus() {
        return loginStatus;
    }
    public void setLoginStatus(LoginStatusEnum loginStatus) {
        this.loginStatus = loginStatus;
    }

    public AuthType getAuthType() {
        return authType;
    }
    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public Instant getLastChangedDate() {
        return lastChangedDate;
    }
    public void setLastChangedDate(Instant lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }

    public boolean isForceReset() {
        return forceReset;
    }
    public void setForceReset(boolean forceReset) {
        this.forceReset = forceReset;
    }

    public Integer getUserGroupId() {
            return userGroupId;
    }
    public void setUserGroupId(Integer userGroupId) {
        if (userGroupId == null || LiteUserGroup.NULL_USER_GROUP_ID ==  userGroupId) {
            this.userGroupId = null;
        } else {
            this.userGroupId = userGroupId;
        }
    }
}
package com.cannontech.database.data.lite;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;

/**
 * This class represents the non-password related bits of a user. For the password-related bits, see
 * {@link AuthenticationService} and {@link UserAuthenticationInfo}.
 */
public class LiteYukonUser extends LiteBase {
    private String username;
    private LoginStatusEnum loginStatus;
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
        this(id, username, loginStatus, false, null);
    }

    public LiteYukonUser(int id, String username, LoginStatusEnum loginStatus, boolean forceReset, Integer userGroupId) {
        setLiteType(LiteTypes.YUKON_USER);
        setLiteID(id);
        this.username = username;
        this.loginStatus = loginStatus;
        this.forceReset = forceReset;
        this.userGroupId = userGroupId;
    }

    public void retrieve( String dbAlias ) {
        YukonJdbcTemplate yukonJdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Username, Status, ForceReset, UserGroupId");
        sql.append("FROM YukonUser");
        sql.append("WHERE UserId").eq(getUserID());

        yukonJdbcTemplate.query(sql, new ResultSetExtractor<LiteYukonUser>() {
            @Override
            public LiteYukonUser extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                username = rs.getString("Username").trim();
                loginStatus = LoginStatusEnum.retrieveLoginStatus(rs.getString("Status"));
                forceReset = "Y".equals(rs.getString("ForceReset"));
                userGroupId = rs.getInt("UserGroupId");
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
        this.userGroupId = userGroupId;
    }
}
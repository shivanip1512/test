package com.cannontech.database.data.lite;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.db.user.YukonUser;

/**
 * @author alauinger
 */
public class LiteYukonUser extends LiteBase {
    private String username;
    private LoginStatusEnum loginStatus;
    private AuthType authType;

    public LiteYukonUser() {
        this(0,null,null);
    }

    public LiteYukonUser(int id) {
        this(id,null,null);
    }

    public LiteYukonUser(int id, String username, LoginStatusEnum loginStatus) {
        this(id, username, loginStatus, AuthType.PLAIN);
    }

    public LiteYukonUser(int id, String username, LoginStatusEnum loginStatus, AuthType authType) {
        setLiteType(LiteTypes.YUKON_USER);
        setUserID(id);
        setUsername(username);
        setLoginStatus(loginStatus);
        setAuthType(authType);
    }

    public void retrieve( String dbAlias )
    {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select", "Username,Status,AuthType");
        sql.append("from", YukonUser.TABLE_NAME);
        sql.append("where", "UserID = ?");

        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();

        Object[] args = new Object[] {getUserID()};
        template.query(sql.toString(), args, new ResultSetExtractor() {
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                setUsername(rs.getString("Username").trim() );
                setLoginStatus(LoginStatusEnum.retrieveLoginStatus(rs.getString("Status")));
                setAuthType(AuthType.valueOf(rs.getString("AuthType")));
                return null;
            }
        });

    }

    /**
     * Returns the username.
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the userID.
     * @return int
     */
    public int getUserID() {
        return getLiteID();
    }

    /**
     * Sets the userID.
     * @param userID The userID to set
     */
    public void setUserID(int userID) {
        setLiteID(userID);
    }

    /**
     * This method was created by Cannon Technologies Inc.
     */
    @Override
    public String toString()
    {
        return getUsername();
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

}

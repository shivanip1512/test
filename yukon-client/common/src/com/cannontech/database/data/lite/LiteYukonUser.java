package com.cannontech.database.data.lite;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.Instant;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.spring.YukonSpringHook;

public class LiteYukonUser extends LiteBase {
    private String username;
    private LoginStatusEnum loginStatus;
    private AuthType authType;
    private Instant lastChangedDate;
    private boolean forceReset;

    public LiteYukonUser() {
        this(0,null,null);
    }

    public LiteYukonUser(int id) {
        this(id,null,null);
    }

    public LiteYukonUser(int id, String username, LoginStatusEnum loginStatus) {
        this(id, username, loginStatus, AuthType.PLAIN, Instant.now(), false);
    }

    public LiteYukonUser(int id, String username, LoginStatusEnum loginStatus, AuthType authType, Instant lastChangedDate, boolean forceReset) {
        setLiteType(LiteTypes.YUKON_USER);
        setUserID(id);
        setUsername(username);
        setLoginStatus(loginStatus);
        setAuthType(authType);
        setLastChangedDate(lastChangedDate);
        setForceReset(forceReset);
    }

    public void retrieve( String dbAlias ) {
        
        YukonJdbcTemplate yukonJdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Username, Status, AuthType, LastChangedDate, ForceReset");
        sql.append("FROM YukonUser");
        sql.append("WHERE  UserId").eq(getUserID());

        yukonJdbcTemplate.query(sql, new ResultSetExtractor<LiteYukonUser>() {
            public LiteYukonUser extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                setUsername(rs.getString("Username").trim() );
                setLoginStatus(LoginStatusEnum.retrieveLoginStatus(rs.getString("Status")));
                setAuthType(AuthType.valueOf(rs.getString("AuthType")));
                setLastChangedDate(new Instant(rs.getDate("lastChangedDate")));
                setForceReset(YNBoolean.valueOf(rs.getString("ForceReset")).getBoolean());
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
}
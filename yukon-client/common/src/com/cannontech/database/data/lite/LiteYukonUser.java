package com.cannontech.database.data.lite;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.db.user.YukonUser;

/**
 * @author alauinger
 */
public class LiteYukonUser extends LiteBase {
    private String username;
    private String status;
    private AuthType authType;

    public LiteYukonUser() {
        this(0,null,null);
    }

    public LiteYukonUser(int id) {
        this(id,null,null);
    }

    public LiteYukonUser(int id, String username, String status) {
        this(id, username, status, AuthType.PLAIN);
    }

    public LiteYukonUser(int id, String username, String status, AuthType authType) {
        setLiteType(LiteTypes.YUKON_USER);
        setUserID(id);
        setUsername(username);
        setStatus(status);
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
                setStatus(rs.getString("Status") );
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

    /**
     * Returns the status.
     * @return String
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param status The status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((authType == null) ? 0 : authType.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LiteYukonUser other = (LiteYukonUser) obj;
        if (authType == null) {
            if (other.authType != null)
                return false;
        } else if (!authType.equals(other.authType))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

}

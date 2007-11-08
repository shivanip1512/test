package com.cannontech.yukon.server.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Loads all yukon users
 * @author alauinger
 */

public final class YukonUserLoader {
    private JdbcOperations jdbcTemplate;
    
    public void load(final List<LiteYukonUser> allUsers, final Map<Integer, LiteYukonUser> allUsersMap) {
        final long timerStart = System.currentTimeMillis();

        String sql = "SELECT UserID,Username,AuthType,Status FROM YukonUser";


        jdbcTemplate.query(sql, new RowCallbackHandler() {
            public void processRow(ResultSet rset) throws SQLException {
                int userID = rset.getInt("UserID");
                String username = rset.getString("Username").trim();
                String status = rset.getString("Status").trim();
                
                AuthType authType = AuthType.valueOf(rset.getString("AuthType"));
                LiteYukonUser user = new LiteYukonUser(userID,username,status,authType); 
                allUsers.add(user);
                allUsersMap.put(user.getUserID(), user );
            }
        });

        double secs = (System.currentTimeMillis() - timerStart)*.001;
        CTILogger.info(secs + " Secs for YukonUserLoader (" + allUsers.size() + " loaded)" );   
    }
    
    @Required
    public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}


package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Loads all yukon users
 * @author alauinger
 */

public final class YukonUserLoader implements Runnable
{
    private final List<LiteYukonUser> allUsers;
    private final Map<Integer, LiteYukonUser> allUsersMap;

    public YukonUserLoader(List<LiteYukonUser> allUsers, Map<Integer, LiteYukonUser> allUsersMap) {
        this.allUsers = allUsers;
        this.allUsersMap = allUsersMap;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        final long timerStart = System.currentTimeMillis();

        String sql = "SELECT UserID,Username,AuthType,Status FROM YukonUser";

        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();

        template.query(sql, new RowCallbackHandler() {
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

}


package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Loads all yukon users
 * @author alauinger
 */

public final class YukonUserLoader implements Runnable
{
	private static final String sql = "SELECT UserID,Username,Password,Status FROM YukonUser";
	
   	private final List allUsers;
	private final Map allUsersMap;
	private final String dbAlias;

	public YukonUserLoader(final ArrayList allUsers, Map allUsersMap, final String dbAlias) {
   		this.allUsers = allUsers;
      	this.dbAlias = dbAlias;
		this.allUsersMap = allUsersMap;
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		final long timerStart = System.currentTimeMillis();

      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try {
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
         	stmt = conn.createStatement();
         	rset = stmt.executeQuery(sql);
   
      		while (rset.next() ) {
      			final int userID = rset.getInt(1);
      			final String username = rset.getString(2).trim();
      			final String password = rset.getString(3).trim();
      			final String status = rset.getString(4).trim();
      			
            	final LiteYukonUser user = new LiteYukonUser(userID,username,password,status);            
           		allUsers.add(user);
				allUsersMap.put( new Integer(user.getUserID()), user );
         	}
      	}
      	catch(SQLException e ) {
         	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      	}
      	finally {
         	try {
            	if( stmt != null )
               		stmt.close();
            	if( conn != null )
               	conn.close();
         	}
         	catch( java.sql.SQLException e ) {
            	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
         	}
   
   		CTILogger.info( 
       (System.currentTimeMillis() - timerStart)*.001 + 
       " Secs for YukonUserLoader (" + allUsers.size() + " loaded)" );   
      }
   
   }

}

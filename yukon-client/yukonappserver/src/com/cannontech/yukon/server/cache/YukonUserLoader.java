package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * @author alauinger
 */

public class YukonUserLoader implements Runnable
{
   	private ArrayList allUsers;
	private String dbAlias = null;

	public YukonUserLoader(ArrayList allUsers, String dbAlias) {
   		this.allUsers = allUsers;
      	this.dbAlias = dbAlias;      	
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		long timerStart = System.currentTimeMillis();
   		
   		String sql = "SELECT UserID,Username,Password FROM YukonUser";
   		
      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try {
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
         	stmt = conn.createStatement();
         	rset = stmt.executeQuery(sql);
   
      		while (rset.next() ) {
      			int userID = rset.getInt(1);
      			String username = rset.getString(2).trim();
      			String password = rset.getString(3).trim();
      			
            	LiteYukonUser user = new LiteYukonUser(userID,username,password);            
           		allUsers.add(user);
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

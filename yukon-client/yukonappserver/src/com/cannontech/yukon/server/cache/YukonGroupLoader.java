package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonGroup;

/**
 * @author alauinger
 */

public class YukonGroupLoader implements Runnable
{
   	private ArrayList allGroups;
	private String dbAlias = null;

	public YukonGroupLoader(ArrayList allGroups, String dbAlias) {
   		this.allGroups = allGroups;
      	this.dbAlias = dbAlias;      	
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		long timerStart = System.currentTimeMillis();
   		
   		String sql = "SELECT GroupID,GroupName FROM YukonGroup";

      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try {
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
         	stmt = conn.createStatement();
         	rset = stmt.executeQuery(sql);
   
      		while (rset.next() ) {      			
      			int groupID = rset.getInt(1);
      			String groupName = rset.getString(2).trim();      			
      			
      			LiteYukonGroup group = new LiteYukonGroup(groupID,groupName);      			
            	allGroups.add(group);                       		
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
       " Secs for YukonGroupLoader (" + allGroups.size() + " loaded)" );   
      }
   
   }

}

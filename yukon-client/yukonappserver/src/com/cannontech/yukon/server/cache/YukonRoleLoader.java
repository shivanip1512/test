package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonRole;

/**
 * @author alauinger
 */

public class YukonRoleLoader implements Runnable
{
   	private ArrayList allRoles;
	private String dbAlias = null;

	public YukonRoleLoader(ArrayList allRoles, String dbAlias) {
   		this.allRoles = allRoles;
      	this.dbAlias = dbAlias;      	
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		long timerStart = System.currentTimeMillis();
   		
   		String sql = "SELECT RoleID,RoleName,Category,DefaultValue,RoleDescription FROM YukonRole";
   		
      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try {
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
         	stmt = conn.createStatement();
         	rset = stmt.executeQuery(sql);
   
      		while (rset.next() ) {      			
      			int roleID = rset.getInt(1);
      			String roleName = rset.getString(2).trim();
      			String category = rset.getString(3).trim();
      			String defaultValue = rset.getString(4).trim();
      			String description = rset.getString(5).trim();
      			      			
      			LiteYukonRole role = new LiteYukonRole(roleID, roleName, category, defaultValue, description);      			      			
            	allRoles.add(role);                       		
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
       " Secs for YukonRoleLoader (" + allRoles.size() + " loaded)" );   
      }
   
   }

}

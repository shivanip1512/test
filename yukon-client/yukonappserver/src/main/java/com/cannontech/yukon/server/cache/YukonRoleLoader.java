package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonRole;

/**
 * @author alauinger
 */

public final class YukonRoleLoader implements Runnable
{
	private static final String sql = "SELECT RoleID,RoleName,Category,RoleDescription FROM YukonRole";
   	final private List<LiteYukonRole> allRoles;
	final private String dbAlias;

	public YukonRoleLoader(List<LiteYukonRole> allRoles, final String dbAlias) {
   		this.allRoles = allRoles;
      	this.dbAlias = dbAlias;      	
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
      			final int roleID = rset.getInt(1);
      			final String roleName = rset.getString(2).trim();
      			final String category = rset.getString(3).trim();
      			final String description = rset.getString(4).trim();
      			      			
      			final LiteYukonRole role = new LiteYukonRole(roleID, roleName, category, description);      			      			
            	allRoles.add(role);                       		
         	}
      	}
      	catch(SQLException e ) {
         	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      	}
      	finally {
            SqlUtils.close(rset, stmt, conn);
   
            CTILogger.info( 
                           (System.currentTimeMillis() - timerStart)*.001 + 
                           " Secs for YukonRoleLoader (" + allRoles.size() + " loaded)" );   
      }
   
   }

}

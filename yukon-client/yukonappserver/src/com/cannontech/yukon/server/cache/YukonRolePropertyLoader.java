package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;

/**
 * Loads all the yukon role properties into a List
 * @author alauinger
 */

public final class YukonRolePropertyLoader implements Runnable
{
	private static final String sql = "SELECT RolePropertyID,RoleID,KeyName,DefaultValue,Description FROM YukonRoleProperty";

   	final private List<LiteYukonRoleProperty> allRoleProperties;
	final private String dbAlias;

	public YukonRolePropertyLoader(List<LiteYukonRoleProperty> allRoleProperties, final String dbAlias) {
   		this.allRoleProperties = allRoleProperties;
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
      			final int rolePropertyID = rset.getInt(1);
      			final int roleID = rset.getInt(2);
      			final String keyName = rset.getString(3).trim();
      			final String defaultValue = rset.getString(4).trim();
      			final String description = rset.getString(5).trim();
      			
      			final LiteYukonRoleProperty roleProperty = new LiteYukonRoleProperty(rolePropertyID, roleID, keyName, defaultValue, description);
            	allRoleProperties.add(roleProperty);                       		
         	}
      	}
      	catch(SQLException e ) {
         	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      	}
      	finally {
            SqlUtils.close(rset, stmt, conn);

            CTILogger.info( 
                           (System.currentTimeMillis() - timerStart)*.001 + 
                           " Secs for YukonRolePropertyLoader (" + allRoleProperties.size() + " loaded)" );   
      }
   
   }

}

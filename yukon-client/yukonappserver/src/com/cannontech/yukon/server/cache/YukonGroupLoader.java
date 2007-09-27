package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.db.user.YukonGroup;

/**
 * @author alauinger
 */

public final class YukonGroupLoader implements Runnable
{
	private static final String sql = 
				"SELECT GroupID, GroupName, GroupDescription FROM " + YukonGroup.TABLE_NAME;
	
   	private final List allGroups;
	private final String dbAlias;

	public YukonGroupLoader(final List allGroups, final String dbAlias) {
   		this.allGroups = allGroups;
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
      			final int groupID = rset.getInt(1);
      			final String groupName = rset.getString(2).trim();      			
					final String groupDesc = rset.getString(3).trim();
					
      			final LiteYukonGroup group = new LiteYukonGroup(groupID,groupName);
      			group.setGroupDescription( groupDesc );


            	allGroups.add(group);                       		
         	}
      	}
      	catch(SQLException e ) {
         	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      	}
      	finally {
            SqlUtils.close(rset, stmt, conn);
   
            CTILogger.info( 
                           (System.currentTimeMillis() - timerStart)*.001 + 
                           " Secs for YukonGroupLoader (" + allGroups.size() + " loaded)" );   
      	}
   
   }

}

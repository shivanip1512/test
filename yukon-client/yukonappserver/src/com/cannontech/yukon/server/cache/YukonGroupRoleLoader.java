package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonGroup;

/**
 * Builds up a Map<LiteYukonGroup,List<LiteYukonRole>>
 * @author alauinger
 */

public class YukonGroupRoleLoader implements Runnable
{
	private Map allGroupRoles;
	private List allGroups;
	private List allRoles;
	private String dbAlias = null;

	public YukonGroupRoleLoader(Map allGroupRoles, List allGroups, List allRoles, String dbAlias) {
		this.allGroupRoles = allGroupRoles;
   		this.allGroups = allGroups;
   		this.allRoles = allRoles;
      	this.dbAlias = dbAlias;      	
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		long timerStart = System.currentTimeMillis();
   		
   		// build up some hashtables to avoid 
   		// exponential algorithm   		
   		HashMap userMap = new HashMap(allGroups.size()*2);
   		HashMap roleMap = new HashMap(allRoles.size()*2);
   		
   		Iterator i = allGroups.iterator();
   		while(i.hasNext()) {
   			LiteYukonGroup g = (LiteYukonGroup) i.next();
   			userMap.put(new Integer(g.getGroupID()), g);
   		}
   		
   		i = allRoles.iterator();
   		while(i.hasNext()) {
   			LiteYukonRole r = (LiteYukonRole) i.next();
   			roleMap.put(new Integer(r.getRoleID()), r);
   		}
   		
   		String sql = "SELECT GroupID,RoleID,Value FROM YukonGroupRole";
   		
      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try {
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
         	stmt = conn.createStatement();
         	rset = stmt.executeQuery(sql);
   
   			Integer lastID = new Integer(Integer.MIN_VALUE);
   		 
      		while (rset.next()) {
      			Integer userID = new Integer(rset.getInt(1));
      			Integer roleID = new Integer(rset.getInt(2));
      			String value = rset.getString(3);
      			
      			LiteYukonGroup group = (LiteYukonGroup) userMap.get(userID);
      			List roleList = (List) allGroupRoles.get(group);
      			if(roleList == null) {
      				roleList = new ArrayList();
      				allGroupRoles.put(group,roleList);
      			}
            	
            	LiteYukonRole role = (LiteYukonRole) roleMap.get(roleID);
            	
            	// If (none) appears in the yukongrouprole.value column then use the default value for the role
            	if(value.equalsIgnoreCase(CtiUtilities.STRING_NONE)) {
            		value = role.getDefaultValue();
            	}
            	
            	Pair roleValuePair = new Pair(role,value);
            	roleList.add(roleValuePair);            		
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
       " Secs for YukonGroupRoleLoader (" + allGroupRoles.size() + " loaded)" );   
      }
   
   }

}

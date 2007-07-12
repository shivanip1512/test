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
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Builds up a maps
 * Map<LiteYukonUser,List<LiteYukonGroup>> and
 * Map<LiteYukonGroup,List<LiteYukonUser>>
 * @author alauinger
 */

public class YukonUserGroupLoader implements Runnable
{
	private Map allUserGroups;
	private Map allGroupUsers;
	private List allUsers;
	private List allGroups;
	private String dbAlias = null;

	public YukonUserGroupLoader(Map allUserGroups, Map allGroupUsers, List allUsers, List allGroups, String dbAlias) {
		this.allUserGroups = allUserGroups;
		this.allGroupUsers = allGroupUsers;
   		this.allUsers = allUsers;
   		this.allGroups = allGroups;
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
   		HashMap userMap = new HashMap(allUsers.size()*2);
   		HashMap groupMap = new HashMap(allGroups.size()*2);
   		
   		Iterator i = allUsers.iterator();
   		while(i.hasNext()) {
   			LiteYukonUser u = (LiteYukonUser) i.next();
   			userMap.put(new Integer(u.getUserID()), u);
   		}
   		
   		i = allGroups.iterator();
   		while(i.hasNext()) {
   			LiteYukonGroup g = (LiteYukonGroup) i.next();
   			groupMap.put(new Integer(g.getGroupID()), g);
   		}
   		
   		String sql = "SELECT UserID,GroupID FROM YukonUserGroup";
   		
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
      			Integer groupID = new Integer(rset.getInt(2));
      			
      			LiteYukonUser user = (LiteYukonUser) userMap.get(userID);
      			LiteYukonGroup group = (LiteYukonGroup) groupMap.get(groupID);
      			
      			// Add to UserGroup Map      			
      			List groupList = (List) allUserGroups.get(user);
      			if(groupList == null) {
      				groupList = new ArrayList();
      				allUserGroups.put(user,groupList);
      			}
            	            	
            	groupList.add(group);    
            	
				// Add to GroupUser Map				
				List userList = (List) allGroupUsers.get(group);
				if(userList == null) {
					userList = new ArrayList();
					allGroupUsers.put(group,userList);
				}
				
				userList.add(user);           	        		
            	        		
         	}
      	}
      	catch(SQLException e ) {
         	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      	}
      	finally {
      		SqlUtils.close(rset, stmt, conn );
   
   		CTILogger.info( 
       (System.currentTimeMillis() - timerStart)*.001 + 
       " Secs for YukonUserGroupLoader (" + allUserGroups.size() + " loaded)" );   
      }
   
   }

}

package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Builds up a Map<LiteYukonUser, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String(value)>>>
 * @author alauinger
 */

public final class YukonUserRoleLoader implements Runnable
{
	private static final String sql = "SELECT UserID,RoleID,RolePropertyID,Value FROM YukonUserRole";
	final private Map allUserRoleProperties;
	final private List allUsers;
	final private List allRoles;
	final private List allRoleProperties;
	final private String dbAlias;

	public YukonUserRoleLoader(final Map allUserRoleProperties, final List allUsers, final List allRoles, final List allRoleProperties, final String dbAlias) {
		this.allUserRoleProperties = allUserRoleProperties;
   		this.allUsers = allUsers;
   		this.allRoles = allRoles;
   		this.allRoleProperties = allRoleProperties;
      	this.dbAlias = dbAlias;      	
   	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
   		final long timerStart = System.currentTimeMillis();
		int propertyCount = 0;
		   		
   		// build up some hashtables to avoid 
   		// exponential algorithm   		
   		final HashMap userMap = new HashMap(allUsers.size()*2);
   		final HashMap roleMap = new HashMap(allRoles.size()*2);
		final HashMap rolePropertyMap = new HashMap(allRoleProperties.size()*2);
		
   		Iterator i = allUsers.iterator();
   		while(i.hasNext()) {
   			LiteYukonUser u = (LiteYukonUser) i.next();
   			userMap.put(new Integer(u.getUserID()), u);
   		}
   		
   		i = allRoles.iterator();
   		while(i.hasNext()) {
   			LiteYukonRole r = (LiteYukonRole) i.next();
   			roleMap.put(new Integer(r.getRoleID()), r);
   		}
   		
 		i = allRoleProperties.iterator();
 		while(i.hasNext()) {
 			LiteYukonRoleProperty p = (LiteYukonRoleProperty) i.next();
 			rolePropertyMap.put(new Integer(p.getRolePropertyID()), p);
 		}
 		
      	Connection conn = null;
      	Statement stmt = null;
      	ResultSet rset = null;
      	try {
        	conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
         	stmt = conn.createStatement();
         	rset = stmt.executeQuery(sql);
   		 
      		while (rset.next()) {
      			final Integer userID = new Integer(rset.getInt(1));
      			final Integer roleID = new Integer(rset.getInt(2));
      			final Integer rolePropertyID = new Integer(rset.getInt(3));
      			String value = rset.getString(4);
      			      			
      			final LiteYukonUser user = (LiteYukonUser) userMap.get(userID);
      			final LiteYukonRole role = (LiteYukonRole) roleMap.get(roleID);
      			final LiteYukonRoleProperty roleProperty = (LiteYukonRoleProperty) rolePropertyMap.get(rolePropertyID);
      			
      			// Check to see if we should use the properties default 
      			if(CtiUtilities.STRING_NONE.equalsIgnoreCase(value)) {
					//we will print a warning and let the exception propogate
      				if( roleProperty == null )
      					CTILogger.warn("Unable to find the entry RoleProperty with a rolePropertyID = " + rolePropertyID + " and roleID = " + roleID );

      				value = roleProperty.getDefaultValue();
      			}
      			
      			Map userRoleMap = (Map) allUserRoleProperties.get(user);
      			if(userRoleMap == null) {
      				userRoleMap = new HashMap();
      				allUserRoleProperties.put(user,userRoleMap);      			
      			}
      			
      			Map userRolePropertyMap = (Map) userRoleMap.get(role);
      			if(userRolePropertyMap == null) {
      				userRolePropertyMap = new HashMap();
      				userRoleMap.put(role, userRolePropertyMap);
      			}
      			
      			userRolePropertyMap.put(roleProperty, value);      
      			propertyCount++;			
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
       " Secs for YukonUserRoleLoader (" + propertyCount + " loaded)" );   
      }
   
   }

}

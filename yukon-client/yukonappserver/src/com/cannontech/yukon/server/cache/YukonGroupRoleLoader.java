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
import com.cannontech.database.data.lite.LiteYukonGroup;

/**
 * Builds up a Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String(value)>>>
 * @author alauinger
 */

public final class YukonGroupRoleLoader implements Runnable
{
	private static final String sql = "SELECT GroupID,RoleID,RolePropertyID,Value FROM YukonGroupRole";
	final private Map allGroupRoleProperties;
	final private List allGroups;
	final private List allRoles;
	final private List allRoleProperties;
	final private String dbAlias;

	public YukonGroupRoleLoader(final Map allGroupRoleProperties, final List allGroups, final List allRoles, final List allRoleProperties, final String dbAlias) {
		this.allGroupRoleProperties = allGroupRoleProperties;
   		this.allGroups = allGroups;
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
   		final HashMap groupMap = new HashMap(allGroups.size()*2);
   		final HashMap roleMap = new HashMap(allRoles.size()*2);
		final HashMap rolePropertyMap = new HashMap(allRoleProperties.size()*2);
		
   		Iterator i = allGroups.iterator();
   		while(i.hasNext()) {
   			LiteYukonGroup g = (LiteYukonGroup) i.next();
   			groupMap.put(new Integer(g.getGroupID()), g);
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
      			final Integer groupID = new Integer(rset.getInt(1));
      			final Integer roleID = new Integer(rset.getInt(2));
      			final Integer rolePropertyID = new Integer(rset.getInt(3));
      			String value = rset.getString(4);
      			      			
      			final LiteYukonGroup group = (LiteYukonGroup) groupMap.get(groupID);
      			final LiteYukonRole role = (LiteYukonRole) roleMap.get(roleID);
      			final LiteYukonRoleProperty roleProperty = (LiteYukonRoleProperty) rolePropertyMap.get(rolePropertyID);
      			
      			// Check to see if we should use the properties default 
      			if(CtiUtilities.STRING_NONE.equalsIgnoreCase(value)) {
					//we will print a warning and let the exception propogate
					if( roleProperty == null )
						CTILogger.warn("Unable to find the RoleProperty entry with a rolePropertyID = " + rolePropertyID + " and roleID = " + roleID );

      				value = roleProperty.getDefaultValue();
      			}
      			
      			Map groupRoleMap = (Map) allGroupRoleProperties.get(group);
      			if(groupRoleMap == null) {
      				groupRoleMap = new HashMap();
      				allGroupRoleProperties.put(group,groupRoleMap);      			
      			}
      			
      			Map groupRolePropertyMap = (Map) groupRoleMap.get(role);
      			if(groupRolePropertyMap == null) {
      				groupRolePropertyMap = new HashMap();
      				groupRoleMap.put(role, groupRolePropertyMap);
      			}
      			
      			groupRolePropertyMap.put(roleProperty, value);      
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
       " Secs for YukonGroupRoleLoader (" + propertyCount + " loaded)" );   
      }
   
   }

}

package com.cannontech.yukon.server.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;

/**
 * Builds up a Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String(value)>>>
 * @author alauinger
 */

public final class YukonGroupRoleLoader implements Runnable
{
	private static final String sql = "SELECT GroupID,RoleID,RolePropertyID,Value FROM YukonGroupRole";
	final private Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> allGroupRoleProperties;
	final private List<LiteYukonGroup> allGroups;
	final private List<LiteYukonRole> allRoles;
	final private List<LiteYukonRoleProperty> allRoleProperties;
	final private String dbAlias;

	public YukonGroupRoleLoader(final Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> allGroupRoleProperties, final List<LiteYukonGroup> allGroups, final List<LiteYukonRole> allRoles, final List<LiteYukonRoleProperty> allRoleProperties, final String dbAlias) {
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
   		final HashMap<Integer, LiteYukonGroup> groupMap = new HashMap<Integer, LiteYukonGroup>(allGroups.size()*2);
   		final HashMap<Integer, LiteYukonRole> roleMap = new HashMap<Integer, LiteYukonRole>(allRoles.size()*2);
		final HashMap<Integer, LiteYukonRoleProperty> rolePropertyMap = new HashMap<Integer, LiteYukonRoleProperty>(allRoleProperties.size()*2);
		
   		Iterator<LiteYukonGroup> groupIter = allGroups.iterator();
   		while(groupIter.hasNext()) {
   			LiteYukonGroup g = groupIter.next();
   			groupMap.put(new Integer(g.getGroupID()), g);
   		}
   		
   		Iterator<LiteYukonRole> roleIter = allRoles.iterator();
   		while(roleIter.hasNext()) {
   			LiteYukonRole r = roleIter.next();
   			roleMap.put(new Integer(r.getRoleID()), r);
   		}
   		
 		Iterator<LiteYukonRoleProperty> rolePropertyIter = allRoleProperties.iterator();
 		while(rolePropertyIter.hasNext()) {
 			LiteYukonRoleProperty p = rolePropertyIter.next();
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
      			      			
      			final LiteYukonGroup group = groupMap.get(groupID);
      			final LiteYukonRole role = roleMap.get(roleID);
      			final LiteYukonRoleProperty roleProperty = rolePropertyMap.get(rolePropertyID);
      			
      			// Check to see if we should use the properties default 
      			if(StringUtils.isBlank(value) || value.trim().equals(CtiUtilities.STRING_NONE)) {
      			    //we will print a warning and let the exception propogate
      			    if (roleProperty == null) {
      			        throw new RuntimeException("Unable to find the RoleProperty entry with a rolePropertyID = " + rolePropertyID + " and roleID = " + roleID );
      			    }

      			    value = roleProperty.getDefaultValue();
      			}
      			
      			Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>> groupRoleMap = allGroupRoleProperties.get(group);
      			if(groupRoleMap == null) {
      				groupRoleMap = new HashMap<LiteYukonRole, Map<LiteYukonRoleProperty, String>>();
      				allGroupRoleProperties.put(group,groupRoleMap);      			
      			}
      			
      			Map<LiteYukonRoleProperty, String> groupRolePropertyMap = groupRoleMap.get(role);
      			if(groupRolePropertyMap == null) {
      				groupRolePropertyMap = new HashMap<LiteYukonRoleProperty, String>();
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
            SqlUtils.close(rset, stmt, conn);

            CTILogger.info( 
                           (System.currentTimeMillis() - timerStart)*.001 + 
                           " Secs for YukonGroupRoleLoader (" + propertyCount + " loaded)" );   
      }
   
   }

}

/*
 * Created on Sep 16, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.database.db.user.YukonUserRole;
import com.cannontech.database.db.user.YukonRole;
import com.cannontech.database.db.user.YukonRoleProperty;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.user.YukonRole;
import com.cannontech.common.util.CtiUtilities;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonUserRolePropertyLookup 
{
	/**
	 * Constructor for YukonUserFuncs.
	 */
	public YukonUserRolePropertyLookup()
	{
		super();
	}
   
	public static String loadSpecificRoleProperty(LiteYukonUser user, int rolePropertyID)
	{
		/*
		 * -First, find out if this guy has a user entry for this role property.
		 * -We obtain the group entry for this property if there is not a user one.
		 * -Return the value field of that entry. 
		 */
		 
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT value FROM YukonUserRole WHERE userid = " + user.getLiteID() +
													" AND RolePropertyID = " + rolePropertyID,
													"yukon" );
		String propertyValue = null;
		
		try
		{
			stmt.execute();
			
			if( stmt.getRowCount() > 0 )
				propertyValue = stmt.getRow(0)[0].toString();
		
		}
		catch( Exception e )
		{
	   		com.cannontech.clientutils.CTILogger.error( "Error retrieving roles for user " + user.getUsername() + ": " + e.getMessage(), e );
		}	
		
		//no user role property, so get the group one...first need to find all the groups for this user, though
		if(propertyValue == null || propertyValue.compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			stmt = new com.cannontech.database.SqlStatement("SELECT GroupID FROM YukonUserGroup WHERE userid = " + user.getLiteID() 
														+ " ORDER BY GROUPID", "yukon" );
			Integer[] groupIDs = null;
		
			try
			{
				stmt.execute();
				
				if( stmt.getRowCount() > 0 )
				{
					groupIDs = new Integer[stmt.getRowCount()];

					for( int i = 0; i < groupIDs.length; i++ )
					{
						groupIDs[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue());
					}
				}
			}
			catch( Exception e )
			{
		  		com.cannontech.clientutils.CTILogger.error( "Error retrieving groups for user " + user.getUsername() + ": " + e.getMessage(), e );
		   		return "";
			}
			
			StringBuffer orState = new StringBuffer("GROUPID = ");
			for(int j = 0; j < groupIDs.length; j++)
			{
				orState.append(groupIDs[j] + " OR GROUPID = ");
			}
			orState.delete(orState.lastIndexOf(" OR GROUPID = "), orState.length());
			orState.append(")");
			
			stmt = new com.cannontech.database.SqlStatement("SELECT value FROM YukonGroupRole WHERE rolepropertyid = " + rolePropertyID + " AND ("
															+ orState, "yukon" );
			try
			{
				stmt.execute();
			
				if( stmt.getRowCount() > 0 )
				{
					//assuming there are no role conflicts, should only have one occurrence for this user
					propertyValue = stmt.getRow(0)[0].toString();
				}
			}
			catch( Exception e )
			{
				com.cannontech.clientutils.CTILogger.error( "Error retrieving role property value for user " + user.getUsername() + ": " + e.getMessage(), e );
			}												
		}
		
		//not sure why we can't find it, but better return default value
		
		if(propertyValue == null || propertyValue.compareTo(CtiUtilities.STRING_NONE) == 0)
		{
			stmt = new com.cannontech.database.SqlStatement("SELECT DefaultValue FROM YukonRoleProperty WHERE rolePropertyID = " + rolePropertyID,
															"yukon" );
			try
			{
				stmt.execute();
			
				if( stmt.getRowCount() > 0 )
					propertyValue = stmt.getRow(0)[0].toString();
			}
			catch( Exception e )
			{
				com.cannontech.clientutils.CTILogger.error( "Role property with id " + rolePropertyID + "does not appear to exist: " + e.getMessage(), e );
				return "";
			}	
		}
		
		return propertyValue;
	}
	
	public static LiteYukonRole loadSpecificRole(LiteYukonUser user, int roleID)
	{
		/*
		 * -First, find out if this guy has a user entry for this role.
		 * -We obtain the group entry for this role if there is not a user one.
		 * -Return the role information as a LiteYukonRole. 
		 */
		 
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT UserRoleID,UserID,RoleID,RolePropertyID,Value FROM YukonUserRole WHERE UserID = " + user.getLiteID()
													+ " AND RoleID = " + roleID,
													"yukon" );
		LiteYukonRole theProudRole = null;
		
		try
		{
			stmt.execute();
			
			//it found one
			if( stmt.getRowCount() > 0 )
			{
				Object[] row = stmt.getRow(0);
			
				YukonRole tempRole = new YukonRole();
				tempRole.setRoleID(new Integer(roleID));
				tempRole = (YukonRole) Transaction.createTransaction(Transaction.RETRIEVE, tempRole).execute();
				theProudRole = new LiteYukonRole();
				theProudRole.setRoleID(roleID);
				theProudRole.setRoleName(tempRole.getRoleName());
				theProudRole.setCategory(tempRole.getCategory());
				theProudRole.setDescription(tempRole.getRoleDescription());
				return theProudRole;
			}
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( "Error retrieving roles for user " + user.getUsername() + ": " + e.getMessage(), e );
			return null;
		}	
		
		//no user role, so get the group one...first need to find all the groups for this user, though
		stmt = new com.cannontech.database.SqlStatement("SELECT GroupID FROM YukonUserGroup WHERE userid = " + user.getLiteID() 
														+ " ORDER BY GROUPID", "yukon" );
		Integer[] groupIDs = null;
		
		try
		{
			stmt.execute();
		
			if( stmt.getRowCount() > 0 )
			{
				groupIDs = new Integer[stmt.getRowCount()];

				for( int i = 0; i < groupIDs.length; i++ )
				{
					groupIDs[i] = new Integer( ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue());
				}
			}
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( "Error retrieving groups for user " + user.getUsername() + ": " + e.getMessage(), e );
		}
			
		StringBuffer orState = new StringBuffer("GROUPID = ");
		for(int j = 0; j < groupIDs.length; j++)
		{
			orState.append(groupIDs[j] + " OR GROUPID = ");
		}
		
		orState.delete(orState.lastIndexOf(" OR GROUPID = "), orState.length());
		orState.append(")");
			
		stmt = new com.cannontech.database.SqlStatement("SELECT GroupRoleID, GroupID, RoleID, RolePropertyID, Value FROM YukonGroupRole WHERE roleid = " + roleID + " AND ("
														+ orState, "yukon" );
		try
		{
			stmt.execute();
			//assuming there are no role conflicts, should only have one occurrence for this user
			//if none, we can assume that this user does not have this role
			if( stmt.getRowCount() > 0 )
			{
				Object[] row = stmt.getRow(0);
			
				YukonRole tempRole = new YukonRole();
				tempRole.setRoleID(new Integer(roleID));
				tempRole = (YukonRole) Transaction.createTransaction(Transaction.RETRIEVE, tempRole).execute();
				theProudRole = new LiteYukonRole();
				theProudRole.setRoleID(roleID);
				theProudRole.setRoleName(tempRole.getRoleName());
				theProudRole.setCategory(tempRole.getCategory());
				theProudRole.setDescription(tempRole.getRoleDescription());
				return theProudRole;
			}
		}
		catch( Exception e )
		{
			com.cannontech.clientutils.CTILogger.error( "Error retrieving role for user " + user.getUsername() + ": " + e.getMessage(), e );
		}												
			
		return theProudRole;
		
	}
	
	
}

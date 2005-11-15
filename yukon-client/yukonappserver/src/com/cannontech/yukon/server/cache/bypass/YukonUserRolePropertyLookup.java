/*
 * Created on Sep 16, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.YukonRole;
import com.cannontech.database.db.user.YukonUserRole;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.database.db.user.YukonRoleProperty;

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
			new com.cannontech.database.SqlStatement("SELECT value FROM " + YukonUserRole.TABLE_NAME + " WHERE userid = " + user.getLiteID() +
													" AND RolePropertyID = " + rolePropertyID,
													CtiUtilities.YUKONDBALIAS );
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
														+ " ORDER BY GROUPID", CtiUtilities.YUKONDBALIAS );
			Integer[] groupIDs = new Integer[0];
		
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

            StringBuffer groupIdStr;
            groupIdStr = convertToSqlLikeList(groupIDs);
			
			stmt = new com.cannontech.database.SqlStatement("SELECT value FROM " + YukonGroupRole.TABLE_NAME + " WHERE rolepropertyid = " + rolePropertyID + " AND GroupID IN("
															+ groupIdStr + ")", CtiUtilities.YUKONDBALIAS );
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
			stmt = new com.cannontech.database.SqlStatement("SELECT DefaultValue FROM " + YukonRoleProperty.TABLE_NAME + " WHERE rolePropertyID = " + rolePropertyID,
															CtiUtilities.YUKONDBALIAS );
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

    private static StringBuffer convertToSqlLikeList(Integer[] groupIDs) {
        StringBuffer groupIdStr;
        if (groupIDs.length > 0) {
            groupIdStr = new StringBuffer();
            for (int j = 0; j < groupIDs.length; j++) {
                if (j != 0) {
                    groupIdStr.append(",");
                }
                groupIdStr.append(groupIDs[j]);
            }
        } else {
            groupIdStr = new StringBuffer("null");
        }
        return groupIdStr;
    }
	
	public static LiteYukonRole loadSpecificRole(LiteYukonUser user, int roleID)
	{
		/*
		 * -First, find out if this guy has a user entry for this role.
		 * -We obtain the group entry for this role if there is not a user one.
		 * -Return the role information as a LiteYukonRole. 
		 */
		 
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement("SELECT UserRoleID,UserID,RoleID,RolePropertyID,Value FROM " + YukonUserRole.TABLE_NAME + " WHERE UserID = " + user.getLiteID()
													+ " AND RoleID = " + roleID,
													CtiUtilities.YUKONDBALIAS );
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
														+ " ORDER BY GROUPID", CtiUtilities.YUKONDBALIAS );
		Integer[] groupIDs = new Integer[0];
		
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
			
		StringBuffer groupIdStr;
		groupIdStr = convertToSqlLikeList(groupIDs);
			
		stmt = new com.cannontech.database.SqlStatement("SELECT GroupRoleID, GroupID, RoleID, RolePropertyID, Value FROM " + YukonGroupRole.TABLE_NAME + " WHERE roleid = " + roleID + " AND GroupID in ("
														+ groupIdStr + ")", CtiUtilities.YUKONDBALIAS );
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

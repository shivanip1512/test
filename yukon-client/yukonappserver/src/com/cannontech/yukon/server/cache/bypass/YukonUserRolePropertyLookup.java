/*
 * Created on Sep 16, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.support.JdbcUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.YukonGroupRole;
import com.cannontech.database.db.user.YukonRole;
import com.cannontech.database.db.user.YukonRoleProperty;
import com.cannontech.database.db.user.YukonUserRole;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonUserRolePropertyLookup 
{
	/**
	 * Constructor for DaoFactory.getYukonUserDao().
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
	    String sql = "SELECT value FROM " + YukonUserRole.TABLE_NAME + 
                    " WHERE userid = ?" +
                    " AND RolePropertyID = ?";

		String propertyValue = null;
		PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        
        conn = PoolManager.getYukonConnection();
        if( conn == null )
        {
            CTILogger.error("YukonUserRolePropertyLookup.loadSpecificRoleProperty:  Error getting database connection.");
            return null;
        }
        try
        {
    		try
    		{
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, user.getLiteID());
                pstmt.setInt(2, rolePropertyID);
    
                rset = pstmt.executeQuery();
                
                if( rset.next())
                    propertyValue = rset.getString(1);
                
    		}
    		catch( Exception e )
    		{
    	   		CTILogger.error( "Error retrieving roles for user " + user.getUsername(), e );
    		} 
            finally 
            {
                JdbcUtils.closeResultSet(rset);
    		    JdbcUtils.closeStatement(pstmt);
            }
    	
    		// no user role property, so get the group one...first need to find all the groups for this user, though
            // The logic here needs to mimic com.cannontech.core.roleproperties.InputTypeFactory.convertPropertyValue(InputType<?>, String).
    		if(StringUtils.isBlank(propertyValue) || propertyValue.trim().equals(CtiUtilities.STRING_NONE))
    		{
    			sql = "SELECT YUG.GroupID, YGR.Value " +
                       " FROM YukonUserGroup yug, " + YukonGroupRole.TABLE_NAME + " ygr " + 
                       " WHERE yug.groupid = ygr.groupid " +
                       " and userid = ?" +
                       " and rolepropertyid = ? " + 
                       " ORDER BY yug.GROUPID";
    
    			try
    			{
                    pstmt = conn.prepareStatement(sql.toString());
                    pstmt.setInt(1, user.getLiteID());
                    pstmt.setInt(2, rolePropertyID);
                    rset = pstmt.executeQuery();
    				
                    if( rset.next())
                    {
                        //assuming there are no role conflicts, should only have one occurrence for this user
                        propertyValue = rset.getString(2);
                    }
    			}
    			catch( Exception e )
    			{
    		  		CTILogger.error( "Error retrieving group, value for user " + user.getUsername(), e );
    		   		return "";
    			}
                finally
                {
                    JdbcUtils.closeResultSet(rset);
                    JdbcUtils.closeStatement(pstmt);
                }
    		}
    		
    		//not sure why we can't find it, but better return default value
            // The logic here needs to mimic com.cannontech.core.roleproperties.InputTypeFactory.convertPropertyValue(InputType<?>, String).    		
    		if(StringUtils.isBlank(propertyValue) || propertyValue.trim().equals(CtiUtilities.STRING_NONE))
    		{
                sql = "SELECT DefaultValue " +
                      " FROM " + YukonRoleProperty.TABLE_NAME + 
                      " WHERE rolePropertyID =?";
    			try
    			{
                    pstmt =  conn.prepareStatement(sql.toString());
                    pstmt.setInt(1, rolePropertyID);
                    rset = pstmt.executeQuery();
                
                    if( rset.next())
                    {
                        //assuming there are no role conflicts, should only have one occurrence for this user
                        propertyValue = rset.getString(1);
                    }
    			}
    			catch( Exception e )
    			{
    				CTILogger.error( "Role property with id " + rolePropertyID + " does not appear to exist", e );
    				return "";
                }
                finally
                {
                    JdbcUtils.closeResultSet(rset);
                    JdbcUtils.closeStatement(pstmt);
                }
    		}
        }
        finally
        {
            JdbcUtils.closeConnection(conn);
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
		 
        String sql = "SELECT yr.RoleID, RoleName, Category, RoleDescription " +
                     " FROM " + YukonUserRole.TABLE_NAME + " yur, " + YukonRole.TABLE_NAME + " yr " +  
                     " WHERE yr.roleid = yur.roleid " +
                     " AND UserID = ?" + 
                     " AND yr.RoleID = ?"; 

		LiteYukonRole theProudRole = null;

        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        
        conn = PoolManager.getYukonConnection();
        if( conn == null )
        {
            CTILogger.error("YukonUserRolePropertyLookup.loadSpecificRole:  Error getting database connection.");
            return null;
        }
        try
        {
    		try
    		{
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, user.getLiteID());
                pstmt.setInt(2, roleID);

                CTILogger.debug(conn.nativeSQL(sql));

                rset = pstmt.executeQuery();

    			//it found one
                if( rset.next())
                {
    				theProudRole = new LiteYukonRole();
    				theProudRole.setRoleID(roleID);
    				theProudRole.setRoleName(rset.getString(2));
    				theProudRole.setCategory(rset.getString(3));
    				theProudRole.setDescription(rset.getString(4));
    			}
    		}
    		catch( Exception e )
    		{
    			CTILogger.error( "Error retrieving roles for user " + user.getUsername(), e );
    			return null;
    		}
            finally
            {
                JdbcUtils.closeResultSet(rset);
                JdbcUtils.closeStatement(pstmt);
            }
    		
            if (theProudRole == null)
            {
                sql = "SELECT Distinct yr.RoleID, RoleName, Category, RoleDescription " +
                      " FROM " + YukonGroupRole.TABLE_NAME + " ygr, YukonUserGroup yug, " + YukonRole.TABLE_NAME + " yr " +
                      " WHERE ygr.groupid = yug.groupid " +
                      " AND ygr.roleid = yr.roleid " +
                      " AND yug.userid = ?" +
                      " AND ygr.roleid = ?"; 
    
        		try
        		{
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, user.getLiteID());
                    pstmt.setInt(2, roleID);
        
                    rset = pstmt.executeQuery();
    
        			//assuming there are no role conflicts, should only have one occurrence for this user
        			//if none, we can assume that this user does not have this role
                    if( rset.next())
        			{
        				theProudRole = new LiteYukonRole();
        				theProudRole.setRoleID(roleID);
        				theProudRole.setRoleName(rset.getString(2));
        				theProudRole.setCategory(rset.getString(3));
        				theProudRole.setDescription(rset.getString(4));
        			}
        		}
        		catch( Exception e )
        		{
        			CTILogger.error( "Error retrieving role for user " + user.getUsername(), e );
        		}
                finally
                {
                    JdbcUtils.closeResultSet(rset);
                    JdbcUtils.closeStatement(pstmt);
                }
            }
		}
        finally
        {
            JdbcUtils.closeConnection(conn);
        }        
		return theProudRole;
	}
}

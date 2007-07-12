package com.cannontech.database.data.lite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.user.YukonGroup;

/**
 * @author alauinger
 */
public class LiteYukonGroup extends LiteBase {
	private String groupName;
	private String groupDescription;
	
	public LiteYukonGroup() {
		initialize(0, null);
	}
	
	public LiteYukonGroup(int id) {
		initialize(id, null);
	}
	
	public LiteYukonGroup(int id, String name) {
		initialize(id, name);
	}
	
	private void initialize(int id, String groupName) {
		setLiteType(LiteTypes.YUKON_GROUP);
		setLiteID(id);
		setGroupName(groupName);
	}
			
	/**
	 * Returns the groupName.
	 * @return String
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets the groupName.
	 * @param groupName The groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * Returns the groupID.
	 * @return int
	 */
	public int getGroupID() {
		return getLiteID();
	}

	/**
	 * Sets the groupID.
	 * @param groupID The groupID to set
	 */
	public void setGroupID(int groupID) {
		setLiteID(groupID);
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString() 
	{
		return getGroupName();
	}
	
	/**
	 * @return String groupDescription
	 */
	public String getGroupDescription()
	{
		return groupDescription;
	}

	/**
	 * @param string
	 */
	public void setGroupDescription(String string)
	{
		groupDescription = string;
	}




	public void retrieve( String dbAlias )
	{
		
		String sql = 
			"SELECT GroupName, GroupDescription FROM " + YukonGroup.TABLE_NAME + " " +
			"WHERE GroupID = " + getGroupID();
   		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		
		try 
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(
							dbAlias );

			stmt = conn.createStatement();
			rset = stmt.executeQuery(sql);

			if( rset.next() ) 
			{
				setGroupName( rset.getString(1).trim() );
				setGroupDescription( rset.getString(2).trim() );
			}
		}
		catch(SQLException e ) 
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally 
		{
			SqlUtils.close(rset, stmt, conn );
		}
      
	}
}

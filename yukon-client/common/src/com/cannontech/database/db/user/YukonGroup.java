package com.cannontech.database.db.user;

import java.sql.SQLException;
import java.util.ArrayList;

import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 */
public class YukonGroup extends DBPersistent 
{
	public static final String TABLE_NAME = "YukonGroup";

	//a mapping table the does not have a DBPersistent
	public static final String TBL_YUKON_USER_GROUP = "YukonUserGroup";


	
	private Integer groupID;
	private String groupName;
	private String groupDescription;
	
	public YukonGroup() {
		initialize(null,null);
	}
	
	public YukonGroup(Integer groupID) {
		initialize(groupID,null);
	}
	
	public YukonGroup(Integer groupID, String groupName) {
		initialize(groupID,groupName);
	}
	
	private void initialize(Integer groupID, String groupName) {
		setGroupID(groupID);
		setGroupName(groupName);
	}


	/**
	 * This method returns a list of YukonGroups that a user belongs to. UserID is the user.
	 * Creation date: (2/14/2003 10:31:33 AM)
	 * @return YukonGroup[]
	 */
	public static synchronized YukonGroup[] getYukonGroups( int userID_, java.sql.Connection conn_ )
	{
		if( conn_ == null )
			throw new IllegalStateException("Database connection should not be null.");
	
		
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		ArrayList list = new ArrayList(16);
		
		try 
		{		
			 pstmt = conn_.prepareStatement(
					"select g.GroupID id, g.GroupName name, g.GroupDescription descr " +
					"from " + TABLE_NAME + " g, " + TBL_YUKON_USER_GROUP + " u " +
					"where u.UserID = ? " +
					"and g.GroupID = u.GroupID " + 
					"order by g.GroupName");
		    	
			 pstmt.setInt( 1, userID_ );
			 
			 rset = pstmt.executeQuery();
				
			 //get the first returned result
			 while( rset.next() )
			 {
				YukonGroup group = new YukonGroup(
					new Integer(rset.getInt("id")),
					rset.getString("name") );

				group.setGroupDescription( rset.getString("descr") );
			 	
				list.add( group );
			 }
		    
		}
		catch (java.sql.SQLException e) 
		{
			 e.printStackTrace();
		}
		finally 
		{
			 try 
			 {
				if ( pstmt != null) pstmt.close();
			 }
			 catch (java.sql.SQLException e2) 
			 {
				e2.printStackTrace();
			 }
		}
		
		YukonGroup[] groups = new YukonGroup[list.size()];
		return (YukonGroup[])list.toArray( groups );
	}

	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = { getGroupID(), getGroupName(), getGroupDescription() };
		add(TABLE_NAME, addValues);
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete(TABLE_NAME, "GroupID", getGroupID());
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		String[] selectColumns = { "GroupName", "GroupDescription" };
		String[] constraintColumns = { "GroupID" };
		Object[] constraintValues = { getGroupID() };
		
		Object[] results = retrieve(selectColumns, TABLE_NAME, constraintColumns, constraintValues);
		if(results.length == selectColumns.length)
		{
			setGroupName((String) results[0]);
			setGroupDescription( (String) results[0] );
		}			
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		String[] setColumns = { "GroupName", "GroupDescription" };
		Object[] setValues = { getGroupName(), getGroupDescription() };
		
		String[] constraintColumns = { "GroupID" };
		Object[] constraintValues = { getGroupID() };
		
		update(TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues);
	}

	/**
	 * Returns the groupID.
	 * @return Integer
	 */
	public Integer getGroupID() {
		return groupID;
	}

	/**
	 * Returns the groupName.
	 * @return String
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets the groupID.
	 * @param groupID The groupID to set
	 */
	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	/**
	 * Sets the groupName.
	 * @param groupName The groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return
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

}

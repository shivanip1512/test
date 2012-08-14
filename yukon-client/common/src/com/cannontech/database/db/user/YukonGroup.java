package com.cannontech.database.db.user;

import java.sql.SQLException;
import java.util.ArrayList;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author alauinger
 */
public class YukonGroup extends DBPersistent {

    public static final String TABLE_NAME = "YukonGroup";

    //what group IDs can be modified
    public static final int EDITABLE_MIN_GROUP_ID  = 0;
    
	// Default group ids
	public static final int YUKON_GROUP_ID = -1;
	public static final int SYSTEM_ADMINISTRATOR_GROUP_ID = -2;
	public static final int OPERATORS_GROUP_ID = -100;
	public static final int ESUB_USERS_GROUP_ID = -200;
	public static final int ESUB_OPERATORS_GROUP_ID = -201;
	public static final int RESIDENTIAL_CUSTOMER_GROUP_ID = -300;
	public static final int WEB_CLIENT_OPERATORS_GROUP_ID = -301;
	public static final int WEB_CLIENT_CUSTOMERS_GROUP_ID = -302;
	public static final int STARS_OPERATORS_GROUP_ID = -303;
	public static final int STARS_RESIDENTIAL_CUSTOMERS_GROUP_ID = -304;
	
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
		ArrayList<YukonGroup> list = new ArrayList<YukonGroup>(16);
		
		try 
		{		
			 pstmt = conn_.prepareStatement(
					"SELECT YG.GroupId id, YG.GroupName name, YG.GroupDescription descr " +
					"FROM YukonGroup YG " +
			        "  JOIN UserGroupToYukonGroupMapping UGYGM ON YG.GroupId = UGYGM.GroupId " +
					"  JOIN YukonUser YU ON UGYGM.UserGroupId = YU.UserGroupId " +
					"WHERE YU.UserId = ? " +
					"ORDER BY YG.GroupName");
		    	
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
			SqlUtils.close(rset, pstmt);
		}
		
		YukonGroup[] groups = new YukonGroup[list.size()];
		return list.toArray( groups );
	}



	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public static final Integer getNextGroupID() {
		NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
		return nextValueHelper.getNextValue("YukonGroup");
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
			setGroupDescription( (String) results[1] );
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

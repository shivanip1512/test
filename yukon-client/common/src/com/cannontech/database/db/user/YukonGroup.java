package com.cannontech.database.db.user;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 */
public class YukonGroup extends DBPersistent {

	public static final String TABLE_NAME = "YukonGroup";
	
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

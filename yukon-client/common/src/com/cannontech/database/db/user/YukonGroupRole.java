package com.cannontech.database.db.user;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 */
public class YukonGroupRole extends DBPersistent 
{
	public static final String TABLE_NAME = "YukonGroupRole";

	public static final String[] SETTER_COLUMNS  = 
	{ 
		"GroupID","RoleID","RolePropertyID","Value" 
	};

	public static final String[] CONSTRAINT_COLUMNS  = { "GroupRoleID" }; 
	
	private Integer groupRoleID = null;
	private Integer groupID = null;
	private Integer roleID = null;
	private Integer rolePropertyID = null;
	private String value = null;

	public YukonGroupRole() {
		super();
	}

	public YukonGroupRole(Integer groupRoleID_, Integer groupID_, Integer roleID_, Integer rolePropertyID_, String value_) {
		this();
		setGroupRoleID(groupRoleID_);
		setGroupID( groupID_ );
		setRoleID( roleID_ );
		setRolePropertyID(rolePropertyID_);
		setValue( value_ );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = 
		{ 
			getGroupRoleID(), getGroupID(), getRoleID(), getRolePropertyID(), getValue() 
		};
		add(TABLE_NAME, addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException 
	{
		Object[] vals = 
		{ 
			getGroupRoleID()
		};
		
		delete(TABLE_NAME, CONSTRAINT_COLUMNS, vals);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException 
	{
		Object[] constraintValues = { getGroupRoleID() };
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		if(results.length == SETTER_COLUMNS.length) 
		{
			setGroupID( (Integer) results[0]);
			setRoleID( (Integer) results[1]);
			setRolePropertyID( (Integer) results[2]);
			setValue( (String) results[3] );
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException 
	{
		Object[] setValues = 
		{ 
			getGroupID(),
			getRoleID(),
			getRolePropertyID(),
			getValue() 
		};

		Object[] constraintValues = { getGroupRoleID() };		
		
		update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
	}

	/**
	 * Returns the roleID.
	 * @return Integer
	 */
	public Integer getRoleID() {
		return roleID;
	}

	/**
	 * Sets the roleID.
	 * @param roleID The roleID to set
	 */
	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
	}


	/**
	 * Returns the groupID.
	 * @return Integer
	 */
	public Integer getGroupID() {
		return groupID;
	}

	/**
	 * Returns the value.
	 * @return String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the groupID.
	 * @param groupID The groupID to set
	 */
	public void setGroupID(Integer userID) {
		this.groupID = userID;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the groupRoleID.
	 * @return Integer
	 */
	public Integer getGroupRoleID() {
		return groupRoleID;
	}

	/**
	 * Sets the groupRoleID.
	 * @param groupRoleID The groupRoleID to set
	 */
	public void setGroupRoleID(Integer userRoleID) {
		this.groupRoleID = userRoleID;
	}

	/**
	 * Returns the rolePropertyID.
	 * @return Integer
	 */
	public Integer getRolePropertyID() {
		return rolePropertyID;
	}

	/**
	 * Sets the rolePropertyID.
	 * @param rolePropertyID The rolePropertyID to set
	 */
	public void setRolePropertyID(Integer rolePropertyID) {
		this.rolePropertyID = rolePropertyID;
	}

}

package com.cannontech.database.db.user;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 */
public class YukonUserRole extends DBPersistent 
{
	public static final String TABLE_NAME = "YukonUserRole";

	public static final String[] SETTER_COLUMNS  = 
	{ 
		"UserID","RoleID","RolePropertyID","Value" 
	};

	public static final String[] CONSTRAINT_COLUMNS  = { "UserRoleID" }; 
	
	private Integer userRoleID = null;
	private Integer userID = null;
	private Integer roleID = null;
	private Integer rolePropertyID = null;
	private String value = null;

	public YukonUserRole() {
		super();
	}

	public YukonUserRole(Integer userRoleID_, Integer userID_, Integer roleID_, Integer rolePropertyID_, String value_) {
		this();
		setUserRoleID(userRoleID_);
		setUserID( userID_ );
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
			getUserRoleID(), getUserID(), getRoleID(), getRolePropertyID(), getValue() 
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
			getUserRoleID()
		};
		
		delete(TABLE_NAME, CONSTRAINT_COLUMNS, vals);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException 
	{
		Object[] constraintValues = { getUserRoleID() };
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		if(results.length == SETTER_COLUMNS.length) 
		{
			setUserID( (Integer) results[0]);
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
			getUserID(),
			getRoleID(),
			getRolePropertyID(),
			getValue() 
		};

		Object[] constraintValues = { getUserRoleID() };		
		
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
	 * Returns the userID.
	 * @return Integer
	 */
	public Integer getUserID() {
		return userID;
	}

	/**
	 * Returns the value.
	 * @return String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the userRoleID.
	 * @return Integer
	 */
	public Integer getUserRoleID() {
		return userRoleID;
	}

	/**
	 * Sets the userRoleID.
	 * @param userRoleID The userRoleID to set
	 */
	public void setUserRoleID(Integer userRoleID) {
		this.userRoleID = userRoleID;
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

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
		"Value" 
	};

	public static final String[] CONSTRAINT_COLUMNS  = { "UserID", "RoleID" }; 
	
	private Integer userID = null;
	private Integer roleID = null;
	private String value = null;


	
	public YukonUserRole() {
		super();
	}
	public YukonUserRole(Integer userID_, Integer roleID_, String value_) {
		this();
		setUserID( userID_ );
		setRoleID( roleID_ );
		setValue( value_ );
	}

		
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = 
		{ 
			getUserID(), getRoleID(), getValue() 
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
			getUserID(), getRoleID() 
		};
		
		delete(TABLE_NAME, CONSTRAINT_COLUMNS, vals);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException 
	{
		Object[] constraintValues = { getUserID(), getRoleID() };
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		if(results.length == SETTER_COLUMNS.length) 
		{
			setValue( (String) results[0] );
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException 
	{
		Object[] setValues = 
		{ 
			getValue() 
		};
		
		Object[] constraintValues = { getUserID(), getRoleID() };
		
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

}

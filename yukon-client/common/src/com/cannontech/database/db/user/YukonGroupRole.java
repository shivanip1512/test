package com.cannontech.database.db.user;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 */
public class YukonGroupRole extends DBPersistent 
{
	public static final String TABLE_NAME = "YukonRoleProperty";

	public static final String[] SETTER_COLUMNS  = 
	{ 
		"RoleID","KeyName","DefaultValue","Description"
	};

	public static final String[] CONSTRAINT_COLUMNS  = { "RolePropertyID" }; 
	
	private Integer rolePropertyID = null;
	private Integer roleID = null;
	private String keyName = null;
	private String defaultValue = null;
	private String description = null;
	
	public YukonGroupRole() {
		super();
	}

	public YukonGroupRole(Integer rolePropertyID, Integer roleID, String keyName, String defaultValue, String description) {
		setRolePropertyID(rolePropertyID);
		setRoleID(roleID);
		setKeyName(keyName);
		setDefaultValue(defaultValue);
		setDescription(description);
	}
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = 
		{ 
			getRolePropertyID(), getRoleID(), getKeyName(), getDefaultValue(), getDescription()
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
			getRolePropertyID()
		};
		
		delete(TABLE_NAME, CONSTRAINT_COLUMNS, vals);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException 
	{
		Object[] constraintValues = { getRolePropertyID() };
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		if(results.length == SETTER_COLUMNS.length) 
		{
			setRoleID((Integer) results[0]);
			setKeyName((String) results[1]);
			setDefaultValue((String) results[2]);
			setDescription((String) results[3]);
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException 
	{
		Object[] setValues = 
		{ 
			getRoleID(),
			getKeyName(),
			getDefaultValue(),
			getDescription()
		};

		Object[] constraintValues = { getRolePropertyID() };		
		
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

	/**
	 * Returns the defaultValue.
	 * @return String
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the keyName.
	 * @return String
	 */
	public String getKeyName() {
		return keyName;
	}

	/**
	 * Sets the defaultValue.
	 * @param defaultValue The defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the keyName.
	 * @param keyName The keyName to set
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

}

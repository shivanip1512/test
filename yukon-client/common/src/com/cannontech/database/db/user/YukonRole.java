package com.cannontech.database.db.user;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 */
public class YukonRole extends DBPersistent 
{
	public static final String TABLE_NAME = "SystemRole";

	public static final String[] SETTER_COLUMNS  = 
	{ 
		"RoleName", "Category", "RoleDescription" 
	};

	public static final String[] CONSTRAINT_COLUMNS  = { "RoleID" }; 
	
	private Integer roleID;
	private String roleName;
	private String category;
	private String roleDescription = CtiUtilities.STRING_NONE;


	
	public YukonRole() {
		initialize(null,null,null);
	}
	
	public YukonRole(Integer roleID) {
		initialize(roleID,null,null);
	}
	
	public YukonRole(Integer roleID, String roleName, String category) {
		initialize(roleID, roleName, category);
	}
	
	private void initialize(Integer roleID, String roleName, String category) {
		setRoleID(roleID);
		setRoleName(roleName);	
	}
		
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = 
		{ 
			getRoleID(), getRoleName(), 
			getCategory(), getRoleDescription() 
		};
		add(TABLE_NAME, addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException 
	{
		delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getRoleID());
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException 
	{
		Object[] constraintValues = { getRoleID() };
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		if(results.length == SETTER_COLUMNS.length) 
		{
			setRoleName((String) results[0]);
			setCategory((String) results[1]);
			setRoleDescription((String) results[2]);
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException 
	{
		Object[] setValues = 
		{ 
			getRoleName(), getCategory(), getRoleDescription() 
		};
		
		Object[] constraintValues = { getRoleID() };
		
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
	 * Returns the roleName.
	 * @return String
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * Sets the roleID.
	 * @param roleID The roleID to set
	 */
	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
	}

	/**
	 * Sets the roleName.
	 * @param roleName The roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * Returns the category.
	 * @return String
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the category.
	 * @param category The category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * Returns the roleDescription.
	 * @return String
	 */
	public String getRoleDescription() {
		return roleDescription;
	}

	/**
	 * Sets the roleDescription.
	 * @param roleDescription The roleDescription to set
	 */
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

}

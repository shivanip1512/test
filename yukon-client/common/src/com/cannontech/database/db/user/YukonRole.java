package com.cannontech.database.db.user;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 */
public class YukonRole extends DBPersistent {
	private static String tableName = "YukonRole";
	
	private Integer roleID;
	private String roleName;
	private String category;
	
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
		Object[] addValues = { getRoleID(), getRoleName(), getCategory() };
		add(tableName, addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete(tableName, "RoleID", getRoleID());
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		String[] selectColumns = { "RoleName", "Category" };
		String[] constraintColumns = { "RoleID"};
		Object[] constraintValues = { getRoleID() };
		
		Object[] results = retrieve(selectColumns, tableName, constraintColumns, constraintValues);
		if(results.length == selectColumns.length) {
			setRoleName((String) results[0]);
			setCategory((String) results[1]);
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		String[] setColumns = { "RoleName", "Category" };
		Object[] setValues = { getRoleName(), getCategory() };
		
		String[] constraintColumns = { "RoleID" };
		Object[] constraintValues = { getRoleID() };
		
		update(tableName, setColumns, setValues, constraintColumns, constraintValues);
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

}

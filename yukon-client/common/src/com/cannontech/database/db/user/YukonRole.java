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
	
	public YukonRole() {
		initialize(null,null);
	}
	
	public YukonRole(Integer roleID) {
		initialize(roleID,null);
	}
	
	public YukonRole(Integer roleID, String roleName) {
		initialize(roleID, roleName);
	}
	
	private void initialize(Integer roleID, String roleName) {
		setRoleID(roleID);
		setRoleName(roleName);	
	}
		
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = { getRoleID() };
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
		String[] selectColumns = { "RoleName" };
		String[] constraintColumns = { "RoleID"};
		Object[] constraintValues = { getRoleID() };
		
		Object[] results = retrieve(selectColumns, tableName, constraintColumns, constraintValues);
		if(results.length == selectColumns.length) {
			setRoleName((String) results[0]);
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		String[] setColumns = { "RoleName" };
		Object[] setValues = { getRoleName() };
		
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

}

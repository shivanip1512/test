package com.cannontech.database.db.user;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 *
 */
public class YukonUser extends DBPersistent {

	private static final String tableName = "YukonUser";
	
	private Integer userID;
	private String username;
	private String password;

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = { getUserID(), getUsername(), getPassword() };
		add(tableName, addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete(tableName, "UserID", getUserID());
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		String[] selectColumns = { "Username", "Password" };
		String[] constraintColumns = { "UserID" };
		Object[] constraintValues = { getUserID() };
		
		Object[] results = retrieve(selectColumns, tableName, constraintColumns, constraintValues);
		
		if(results.length == selectColumns.length) {
			setUsername((String) results[0]);
			setPassword((String) results[1]);
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		String[] setColumns = { "Username", "Password" };
		Object[] setValues = { getUsername(), getPassword() };
		
		String[] constraintColumns = { "UserID" };
		Object[] constraintValues = { getUserID() };
		
		update(tableName, setColumns, setValues, constraintColumns, constraintValues);
	}

	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public Integer getUserID() {
		return userID;
	}

	/**
	 * Returns the username.
	 * @return String
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the password.
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	/**
	 * Sets the username.
	 * @param username The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

}

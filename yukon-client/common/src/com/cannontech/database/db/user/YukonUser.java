package com.cannontech.database.db.user;

import java.sql.SQLException;
import java.util.Date;

import com.cannontech.database.db.DBPersistent;

/**
 * @author alauinger
 */
public class YukonUser extends DBPersistent {

	public static final Integer INVALID_ID = new Integer(Integer.MIN_VALUE);
	private static final String tableName = "YukonUser";	
		
	private Integer userID = INVALID_ID;
	private String username;
	private String password;
	private Integer loginCount;
	private Date lastLogin;
	private String status;
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = { getUserID(), getUsername(), getPassword(), getLoginCount(), getLastLogin(), getStatus() };
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
		String[] selectColumns = { "Username", "Password", "LoginCount", "LastLogin", "Status" };
		String[] constraintColumns = { "UserID" };
		Object[] constraintValues = { getUserID() };
		
		Object[] results = retrieve(selectColumns, tableName, constraintColumns, constraintValues);
		
		if(results.length == selectColumns.length) {
			setUsername((String) results[0]);
			setPassword((String) results[1]);
			setLoginCount((Integer) results[2]);
			setLastLogin((Date) results[3]);
			setStatus((String) results[4]);
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		String[] setColumns = { "Username", "Password", "LoginCount", "LastLogin", "Status" };
		Object[] setValues = { getUsername(), getPassword(), getLoginCount(), getLastLogin(), getStatus() };
		
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

	/**
	 * Returns the lastLogin.
	 * @return Date
	 */
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * Returns the loginCount.
	 * @return Integer
	 */
	public Integer getLoginCount() {
		return loginCount;
	}

	/**
	 * Returns the status.
	 * @return String
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the lastLogin.
	 * @param lastLogin The lastLogin to set
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * Sets the loginCount.
	 * @param loginCount The loginCount to set
	 */
	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}

	/**
	 * Sets the status.
	 * @param status The status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}

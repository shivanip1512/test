package com.cannontech.database.data.lite;

/**
 * @author alauinger
 */
public class LiteYukonUser extends LiteBase {
	private String username;
	private String password;
	
	public LiteYukonUser() {
		initialize(0,null,null);
	}
	
	public LiteYukonUser(int id) {
		initialize(id,null,null);
	}
	
	public LiteYukonUser(int id, String username, String password) {
		initialize(id,username,password);		
	}
	
	private void initialize(int id, String username, String password) {
		setLiteType(LiteTypes.YUKON_USER);
		setUserID(id);
		setUsername(username);
		setPassword(password);
	}
	
	/**
	 * Returns the password.
	 * @return String
	 */
	public String getPassword() {
		return password;
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
	 * Sets the username.
	 * @param username The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns the userID.
	 * @return int
	 */
	public int getUserID() {
		return getLiteID();
	}

	/**
	 * Sets the userID.
	 * @param userID The userID to set
	 */
	public void setUserID(int userID) {
		setLiteID(userID);
	}
}

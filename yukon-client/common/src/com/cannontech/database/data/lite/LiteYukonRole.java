package com.cannontech.database.data.lite;

/**
 * @author alauinger
 */
public class LiteYukonRole extends LiteBase {	
	private String roleName;
	private String defaultValue;
	
	public LiteYukonRole() {
		initialize(0,null,null);
	}

	public LiteYukonRole(int roleID) {
		initialize(roleID,null,null);
	}

	public LiteYukonRole(int roleID, String roleName, String defaultValue) {
		initialize(roleID,roleName,defaultValue);
	}
	
	private void initialize(int roleID, String roleName, String defaultValue) {
		setLiteType(LiteTypes.YUKON_ROLE);
		setRoleID(roleID);
		setRoleName(roleName);		
		setDefaultValue(defaultValue);
	}
	
	/**
	 * Returns the roleName	 
	 * @return String
	 */
	public String getRoleName() {
		return roleName;
	}
	
	/**
	 * Sets the roleName	  
	 * @param roleName
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * Returns the roleID.
	 * @return int
	 */
	public int getRoleID() {
		return getLiteID();	 
	}

	/**
	 * Sets the roleID.
	 * @param roleID The roleID to set
	 */
	public void setRoleID(int roleID) {
		setLiteID(roleID);
	}

	/**
	 * Returns the defaultValue.
	 * @return String
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the defaultValue.
	 * @param defaultValue The defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}

package com.cannontech.database.data.lite;

/**
 * @author alauinger
 */
public class LiteYukonRoleProperty extends LiteBase {	
	
	private int roleID;
	private String keyName;
	private String defaultValue;
	private String description;
	
	public LiteYukonRoleProperty() {
		initialize(0,0,null,null,null);
	}

	public LiteYukonRoleProperty(int rolePropertyID) {
		initialize(rolePropertyID,0,null,null,null);
	}

	public LiteYukonRoleProperty(int rolePropertyID, int roleID, String keyName, String defaultValue, String description) {
		initialize(rolePropertyID, roleID, keyName, defaultValue, description);
	}
	
	private void initialize(int rolePropertyID, int roleID, String keyName, String defaultValue, String description) {
		setLiteType(LiteTypes.YUKON_ROLE_PROPERTY);
		setRolePropertyID(rolePropertyID);
		setRoleID(roleID);
		setKeyName(keyName);
		setDefaultValue(defaultValue);
		setDescription(description);
}
	
	/**
	 * Returns the rolePropertyID.
	 * @return int
	 */
	public int getRolePropertyID() {
		return getLiteID();	 
	}

	/**
	 * Sets the rolePropertyID.
	 * @param roleID The rolePropertyID to set
	 */
	public void setRolePropertyID(int rolePropertyID) {
		setLiteID(rolePropertyID);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getKeyName();
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the defaultValue.
	 * @return String
	 */
	public String getDefaultValue() {
		return defaultValue;
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
	 * Sets the keyName.
	 * @param keyName The keyName to set
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * Returns the roleID.
	 * @return int
	 */
	public int getRoleID() {
		return roleID;
	}

	/**
	 * Sets the roleID.
	 * @param roleID The roleID to set
	 */
	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

}

package com.cannontech.database.data.lite;

/**
 * @author alauinger
 */
public class LiteYukonRole extends LiteBase {	
	private String roleName;
	private String category;
	private String defaultValue;
	private String description;
	
	public LiteYukonRole() {
		initialize(0,null,null,null,null);
	}

	public LiteYukonRole(int roleID) {
		initialize(roleID,null,null,null,null);
	}

	public LiteYukonRole(int roleID, String roleName, String category, String defaultValue, String description) {
		initialize(roleID,roleName,category, defaultValue, description);
	}
	
	private void initialize(int roleID, String roleName, String category, String defaultValue, String description) {
		setLiteType(LiteTypes.YUKON_ROLE);
		setRoleID(roleID);
		setRoleName(roleName);		
		setCategory(category);
		setDefaultValue(defaultValue);
		setDescription(description);
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
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getRoleName();
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

}

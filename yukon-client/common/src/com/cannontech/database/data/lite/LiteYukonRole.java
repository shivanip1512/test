package com.cannontech.database.data.lite;

/**
 * @author alauinger
 */
public class LiteYukonRole extends LiteBase {	
	private String roleName;
	private String category;
	private String description;
	
	public LiteYukonRole() {
		initialize(0,null,null,null);
	}

	public LiteYukonRole(final int roleID) {
		initialize(roleID,null,null,null);
	}

	public LiteYukonRole(final int roleID, final String roleName, final String category, final String description) {
		initialize(roleID,roleName,category, description);
	}
	
	private void initialize(final int roleID, final String roleName, final String category, final String description) {
		setLiteType(LiteTypes.YUKON_ROLE);
		setRoleID(roleID);
		setRoleName(roleName);		
		setCategory(category);
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

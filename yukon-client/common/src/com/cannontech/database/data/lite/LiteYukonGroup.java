package com.cannontech.database.data.lite;

/**
 * @author alauinger
 */
public class LiteYukonGroup extends LiteBase {
	private String groupName;
	
	public LiteYukonGroup() {
		initialize(0, null);
	}
	
	public LiteYukonGroup(int id) {
		initialize(id, null);
	}
	
	public LiteYukonGroup(int id, String name) {
		initialize(id, name);
	}
	
	private void initialize(int id, String groupName) {
		setLiteType(LiteTypes.YUKON_GROUP);
		setLiteID(id);
		setGroupName(groupName);
	}
			
	/**
	 * Returns the groupName.
	 * @return String
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Sets the groupName.
	 * @param groupName The groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * Returns the groupID.
	 * @return int
	 */
	public int getGroupID() {
		return getLiteID();
	}

	/**
	 * Sets the groupID.
	 * @param groupID The groupID to set
	 */
	public void setGroupID(int groupID) {
		setLiteID(groupID);
	}

}

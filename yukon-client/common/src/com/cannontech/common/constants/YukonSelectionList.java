package com.cannontech.common.constants;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class YukonSelectionList {
	
	private int listID = 0;
	private String ordering = null;
	private String selectionLable = null;
	private String whereIsList = null;
	private String listName = null;
	private String userUpdateAvailable = null;
	
	private java.util.Properties yukonListEntries = null;
	
	
	public static final String TABLE_NAME = "YukonSelectionList";
	
	/**
	 * Constructor for YukonSelectionList.
	 */
	public YukonSelectionList() {
		super();
	}

	/**
	 * Returns the tABLE_NAME.
	 * @return String
	 */
	public static String getTABLE_NAME() {
		return TABLE_NAME;
	}

	/**
	 * Returns the listID.
	 * @return int
	 */
	public int getListID() {
		return listID;
	}

	/**
	 * Returns the listName.
	 * @return String
	 */
	public String getListName() {
		return listName;
	}

	/**
	 * Returns the ordering.
	 * @return String
	 */
	public String getOrdering() {
		return ordering;
	}

	/**
	 * Returns the selectionLable.
	 * @return String
	 */
	public String getSelectionLable() {
		return selectionLable;
	}

	/**
	 * Returns the userUpdateAvailable.
	 * @return String
	 */
	public String getUserUpdateAvailable() {
		return userUpdateAvailable;
	}

	/**
	 * Returns the whereIsList.
	 * @return String
	 */
	public String getWhereIsList() {
		return whereIsList;
	}

	/**
	 * Sets the listID.
	 * @param listID The listID to set
	 */
	public void setListID(int listID) {
		this.listID = listID;
	}

	/**
	 * Sets the listName.
	 * @param listName The listName to set
	 */
	public void setListName(String listName) {
		this.listName = listName;
	}

	/**
	 * Sets the ordering.
	 * @param ordering The ordering to set
	 */
	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	/**
	 * Sets the selectionLable.
	 * @param selectionLable The selectionLable to set
	 */
	public void setSelectionLable(String selectionLable) {
		this.selectionLable = selectionLable;
	}

	/**
	 * Sets the userUpdateAvailable.
	 * @param userUpdateAvailable The userUpdateAvailable to set
	 */
	public void setUserUpdateAvailable(String userUpdateAvailable) {
		this.userUpdateAvailable = userUpdateAvailable;
	}

	/**
	 * Sets the whereIsList.
	 * @param whereIsList The whereIsList to set
	 */
	public void setWhereIsList(String whereIsList) {
		this.whereIsList = whereIsList;
	}

	/**
	 * Returns the yukonListEntries.
	 * @return java.util.Properties
	 */
	public java.util.Properties getYukonListEntries() {
		if (yukonListEntries == null)
			yukonListEntries = new java.util.Properties();
		return yukonListEntries;
	}

	/**
	 * Sets the yukonListEntries.
	 * @param yukonListEntries The yukonListEntries to set
	 */
	public void setYukonListEntries(java.util.Properties yukonListEntries) {
		this.yukonListEntries = yukonListEntries;
	}

}

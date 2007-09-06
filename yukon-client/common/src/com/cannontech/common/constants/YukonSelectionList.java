package com.cannontech.common.constants;

import java.util.ArrayList;
import java.util.List;

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
	private String selectionLabel = null;
	private String whereIsList = null;
	private String listName = null;
	private String userUpdateAvailable = null;
	
	private List<YukonListEntry> yukonListEntries = null;
	
	
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
	 * Returns the selectionLabel.
	 * @return String
	 */
	public String getSelectionLabel() {
		return selectionLabel;
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
	 * Sets the selectionLabel.
	 * @param selectionLabel The selectionLabel to set
	 */
	public void setSelectionLabel(String selectionLabel) {
		this.selectionLabel = selectionLabel;
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
	 * @return java.util.ArrayList
	 */
	public List<YukonListEntry> getYukonListEntries() {
		if (yukonListEntries == null)
			yukonListEntries = new ArrayList<YukonListEntry>();
		return yukonListEntries;
	}

	/**
	 * Sets the yukonListEntries.
	 * @param yukonListEntries The yukonListEntries to set
	 */
	public void setYukonListEntries(List<YukonListEntry> yukonListEntries) {
		this.yukonListEntries = yukonListEntries;
	}

}

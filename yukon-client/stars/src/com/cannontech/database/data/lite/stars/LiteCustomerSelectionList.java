package com.cannontech.database.data.lite.stars;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteCustomerSelectionList extends LiteBase {

	private String listName = null;
	private StarsSelectionListEntry[] listEntries = null;
	
	public LiteCustomerSelectionList() {
		super();
	}
	
	public LiteCustomerSelectionList(int listID) {
		super();
		setListID( listID );
		setLiteType( LiteTypes.STARS_CUSTOMER_SELECTION_LIST );
	}
	
	public int getListID() {
		return getLiteID();
	}
	
	public void setListID(int listID) {
		setLiteID( listID );
	}
	/**
	 * Returns the listEntries.
	 * @return StarsSelectionListEntry[]
	 */
	public StarsSelectionListEntry[] getListEntries() {
		return listEntries;
	}

	/**
	 * Returns the listName.
	 * @return String
	 */
	public String getListName() {
		return listName;
	}

	/**
	 * Sets the listEntries.
	 * @param listEntries The listEntries to set
	 */
	public void setListEntries(StarsSelectionListEntry[] listEntries) {
		this.listEntries = listEntries;
	}

	/**
	 * Sets the listName.
	 * @param listName The listName to set
	 */
	public void setListName(String listName) {
		this.listName = listName;
	}

}

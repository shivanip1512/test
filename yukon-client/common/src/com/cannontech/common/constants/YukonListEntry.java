package com.cannontech.common.constants;

import java.util.Vector;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class YukonListEntry 
{
	private int entryID = 0;
	private int listID = 0;
	private int entryOrder = 0;
	private String entryText = null;
	private int yukonDefID = 0;

	//contains instaces of com.cannontech.common.constants.YukonSelectionList
	//private Vector yukonSelectionList = null;
	
	
	public static final String TABLE_NAME = "YukonListEntry";


	/**
	 * Constructor for YukonListEntry.
	 */
	protected YukonListEntry() {
		super();
	}

	/**
	 * Returns the entryID.
	 * @return int
	 */
	public int getEntryID() {
		return entryID;
	}

	/**
	 * Returns the entryText.
	 * @return String
	 */
	public String getEntryText() {
		return entryText;
	}

	/**
	 * Returns the listID.
	 * @return int
	 */
	public int getListID() {
		return listID;
	}

	/**
	 * Returns the listOrder.
	 * @return int
	 */
	public int getEntryOrder() {
		return entryOrder;
	}

	/**
	 * Returns the yukonDefID.
	 * @return int
	 */
	public int getYukonDefID() {
		return yukonDefID;
	}

//	/**
//	 * Returns the yukonSelectionList.
//	 * @return Vector
//	 */
//	public Vector getYukonSelectionList() 
//	{		
//		if( yukonSelectionList == null )
//			yukonSelectionList = new Vector(5);
//
//		return yukonSelectionList;
//	}

	/**
	 * Sets the entryID.
	 * @param entryID The entryID to set
	 */
	public void setEntryID(int entryID) {
		this.entryID = entryID;
	}

	/**
	 * Sets the entryText.
	 * @param entryText The entryText to set
	 */
	public void setEntryText(String entryText) {
		this.entryText = entryText;
	}

	/**
	 * Sets the listID.
	 * @param listID The listID to set
	 */
	public void setListID(int listID) {
		this.listID = listID;
	}

	/**
	 * Sets the listOrder.
	 * @param listOrder The listOrder to set
	 */
	public void setEntryOrder(int entryOrder) {
		this.entryOrder = entryOrder;
	}

	/**
	 * Sets the yukonDefID.
	 * @param yukonDefID The yukonDefID to set
	 */
	public void setYukonDefID(int yukonDefID) {
		this.yukonDefID = yukonDefID;
	}

	/**
	 * Returns the toSting() call
	 * @return String
	 */
	public String toString() {
		return getEntryText();
	}

}

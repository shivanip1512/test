package com.cannontech.database.data.lite.stars;

import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsLMProgram {

	private int lmProgramID = 0;
	private int groupID = 0;
	private java.util.ArrayList programHistory = null;	// List of LiteLMCustomerEvent
	
	public LiteStarsLMProgram() {
	}
	
	/**
	 * Returns the groupID.
	 * @return int
	 */
	public int getGroupID() {
		return groupID;
	}

	/**
	 * Returns the lmProgramID.
	 * @return int
	 */
	public int getLmProgramID() {
		return lmProgramID;
	}

	/**
	 * Sets the groupID.
	 * @param groupID The groupID to set
	 */
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	/**
	 * Sets the lmProgramID.
	 * @param lmProgramID The lmProgramID to set
	 */
	public void setLmProgramID(int lmProgramID) {
		this.lmProgramID = lmProgramID;
	}

	/**
	 * Returns the programHistory.
	 * @return java.util.ArrayList
	 */
	public java.util.ArrayList getProgramHistory() {
		return programHistory;
	}

	/**
	 * Sets the programHistory.
	 * @param programHistory The programHistory to set
	 */
	public void setProgramHistory(java.util.ArrayList programHistory) {
		this.programHistory = programHistory;
	}

}

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
public class LiteStarsLMProgram extends LiteLMProgram {

	private int groupID = 0;
	private java.util.ArrayList programHistory = null;	// List of LiteLMCustomerEvent
	
	public LiteStarsLMProgram() {
		super();
	}
	
	public LiteStarsLMProgram(int programID) {
		super( programID );
	}
	
	public LiteStarsLMProgram(LiteLMProgram program) {
		super( program.getProgramID(), program.getProgramName(), program.getWebSettingsID(), program.getProgramCategory() );
	}
	
	/**
	 * Returns the groupID.
	 * @return int
	 */
	public int getGroupID() {
		return groupID;
	}

	/**
	 * Sets the groupID.
	 * @param groupID The groupID to set
	 */
	public void setGroupID(int groupID) {
		this.groupID = groupID;
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

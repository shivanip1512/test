package com.cannontech.database.data.lite.stars;



/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteStarsLMProgram {

	private LiteLMProgram lmProgram = null;
	private int groupID = 0;
	private java.util.ArrayList programHistory = null;	// List of LiteLMCustomerEvent
	private boolean inService = false;
	
	public LiteStarsLMProgram() {
		super();
	}
	
	public LiteStarsLMProgram(int programID) {
		lmProgram = new LiteLMProgram( programID );
	}
	
	public LiteStarsLMProgram(LiteLMProgram program) {
		lmProgram = program;
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
		if (programHistory == null)
			programHistory = new java.util.ArrayList();
		return programHistory;
	}

	/**
	 * Sets the programHistory.
	 * @param programHistory The programHistory to set
	 */
	public void setProgramHistory(java.util.ArrayList programHistory) {
		this.programHistory = programHistory;
	}

	/**
	 * Returns the lmProgram.
	 * @return LiteLMProgram
	 */
	public LiteLMProgram getLmProgram() {
		return lmProgram;
	}

	/**
	 * Sets the lmProgram.
	 * @param lmProgram The lmProgram to set
	 */
	public void setLmProgram(LiteLMProgram lmProgram) {
		this.lmProgram = lmProgram;
	}

	/**
	 * Returns the inService.
	 * @return boolean
	 */
	public boolean isInService() {
		return inService;
	}

	/**
	 * Sets the inService.
	 * @param inService The inService to set
	 */
	public void setInService(boolean inService) {
		this.inService = inService;
	}

}

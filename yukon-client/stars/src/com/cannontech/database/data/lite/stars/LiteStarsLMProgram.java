package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.cache.functions.YukonListFuncs;

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
	
	public void updateProgramStatus(ArrayList programHistory) {
		for (int i = programHistory.size() - 1; i >= 0 ; i--) {
			LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) programHistory.get(i);
			if (liteEvent.getProgramID() != getLmProgram().getProgramID())
				continue;
			
			YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent.getActionID() );
			
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP)
			{
				inService = true;
				return;
			}
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION)
			{
				inService = false;
				return;
			}
		}
		
		inService = false;
	}

}

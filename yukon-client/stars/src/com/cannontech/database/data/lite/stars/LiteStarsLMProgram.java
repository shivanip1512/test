package com.cannontech.database.data.lite.stars;

import java.util.ArrayList;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
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

	private LiteLMProgramWebPublishing publishedProgram = null;
	private int groupID = CtiUtilities.NONE_ID;
	private boolean inService = false;
	
	public LiteStarsLMProgram() {
		super();
	}
	
	public int getProgramID() {
		return getPublishedProgram().getProgramID();
	}
	
	public LiteStarsLMProgram(int publishingID) {
		publishedProgram = new LiteLMProgramWebPublishing( publishingID );
	}
	
	public LiteStarsLMProgram(LiteLMProgramWebPublishing program) {
		publishedProgram = program;
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
	 * Returns the publishedProgram.
	 * @return LiteLMProgramWebPublishing
	 */
	public LiteLMProgramWebPublishing getPublishedProgram() {
		return publishedProgram;
	}

	/**
	 * Sets the publishedProgram.
	 * @param publishedProgram The publishedProgram to set
	 */
	public void setPublishedProgram(LiteLMProgramWebPublishing publishedProgram) {
		this.publishedProgram = publishedProgram;
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
			if (liteEvent.getProgramID() != getProgramID())
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

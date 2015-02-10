package com.cannontech.stars.database.data.lite;

import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.spring.YukonSpringHook;

public class LiteStarsLMProgram {

	private LiteLMProgramWebPublishing publishedProgram = null;
	private int groupID = CtiUtilities.NONE_ZERO_ID;
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
	
	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	public LiteLMProgramWebPublishing getPublishedProgram() {
		return publishedProgram;
	}

	public void setPublishedProgram(LiteLMProgramWebPublishing publishedProgram) {
		this.publishedProgram = publishedProgram;
	}

	public boolean isInService() {
		return inService;
	}
	
	public void updateProgramStatus(List<LiteLMProgramEvent> programHistory) {
		for (int i = programHistory.size() - 1; i >= 0 ; i--) {
			LiteLMProgramEvent liteEvent = programHistory.get(i);
			if (liteEvent.getProgramID() != getProgramID())
				continue;
			
			YukonListEntry entry = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( liteEvent.getActionID() );
			
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
		
		inService = true;
	}

}

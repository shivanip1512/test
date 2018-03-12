package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CommandRequestExecutionStatus implements DisplayableEnum {

    @Deprecated IN_PROGRESS, // may still exist in a customer's DB, no longer written
	STARTED,
	COMPLETE,
	FAILED,
	CANCELING,
	CANCELLED;
	
	@Override
	public String getFormatKey() {
	
		return "yukon.web.modules.amr.commandRequestExecution.status." + this;
	}
	
	public static List<CommandRequestExecutionStatus> getRecentResultFilterValues() {
	    List<CommandRequestExecutionStatus> retValue = new ArrayList<>();
        retValue.add(STARTED);
        retValue.add(COMPLETE);
        retValue.add(FAILED);
        retValue.add(CANCELLED);
	    return retValue;
	}
}

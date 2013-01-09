package com.cannontech.common.device.commands;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CommandRequestExecutionStatus implements DisplayableEnum {

    @Deprecated IN_PROGRESS, // may still exist in a customer's DB, no longer written
	STARTED,
	COMPLETE,
	FAILED,
	CANCELLED;
	
	@Override
	public String getFormatKey() {
	
		return "yukon.web.modules.amr.commandRequestExecution.status." + this;
	}
}

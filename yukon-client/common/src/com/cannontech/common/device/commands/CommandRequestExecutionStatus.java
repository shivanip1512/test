package com.cannontech.common.device.commands;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CommandRequestExecutionStatus implements DisplayableEnum {

	IN_PROGRESS,
	COMPLETE,
	FAILED;
	
	@Override
	public String getFormatKey() {
	
		return "yukon.web.modules.amr.commandRequestExecution.status." + this;
	}
}

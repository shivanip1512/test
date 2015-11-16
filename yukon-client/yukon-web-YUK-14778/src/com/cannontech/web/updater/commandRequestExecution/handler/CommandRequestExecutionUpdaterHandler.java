package com.cannontech.web.updater.commandRequestExecution.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;

public interface CommandRequestExecutionUpdaterHandler {

	public String handle(int id, YukonUserContext userContext);
	
	public CommandRequestExecutionUpdaterTypeEnum getUpdaterType();
}

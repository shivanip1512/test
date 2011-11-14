package com.cannontech.amr.scheduledGroupRequestExecution.dao;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ScheduledGroupRequestExecutionStatus implements DisplayableEnum {

	ENABLED,
	DISABLED,
	RUNNING,
	DELETED;
	
	public String getFormatKey() {
		return "yukon.common.device.scheduledGroupRequestExecution.state." + this.name();
	}
}

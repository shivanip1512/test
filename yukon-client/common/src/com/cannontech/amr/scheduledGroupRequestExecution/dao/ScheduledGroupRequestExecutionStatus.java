package com.cannontech.amr.scheduledGroupRequestExecution.dao;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ScheduledGroupRequestExecutionStatus implements DisplayableEnum {

	ENABLED,
	DISABLED,
	RUNNING,
	DELETED,
	//Manual status is for display only
	MANUAL;
	
	@Override
    public String getFormatKey() {
		return "yukon.common.device.schedules.state." + this.name();
	}
}

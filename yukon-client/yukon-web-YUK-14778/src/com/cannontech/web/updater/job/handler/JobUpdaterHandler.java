package com.cannontech.web.updater.job.handler;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.job.JobUpdaterTypeEnum;

public interface JobUpdaterHandler {

	public String handle(int jobId, YukonUserContext userContext);
	
	public JobUpdaterTypeEnum getUpdaterType();
}

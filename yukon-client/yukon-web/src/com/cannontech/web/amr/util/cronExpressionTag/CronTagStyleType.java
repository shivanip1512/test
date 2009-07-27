package com.cannontech.web.amr.util.cronExpressionTag;

import com.cannontech.web.amr.util.cronExpressionTag.handler.CronTagStyleHandler;
import com.cannontech.web.amr.util.cronExpressionTag.handler.CustomCronTagStyleHandler;
import com.cannontech.web.amr.util.cronExpressionTag.handler.DailyCronTagStyleHandler;
import com.cannontech.web.amr.util.cronExpressionTag.handler.MonthlyCronTagStyleHandler;
import com.cannontech.web.amr.util.cronExpressionTag.handler.OneTimeCronTagStyleHandler;
import com.cannontech.web.amr.util.cronExpressionTag.handler.WeeklyCronTagStyleHandler;

public enum CronTagStyleType {

	DAILY(new DailyCronTagStyleHandler()),
	WEEKLY(new WeeklyCronTagStyleHandler()),
	MONTHLY(new MonthlyCronTagStyleHandler()),
	ONETIME(new OneTimeCronTagStyleHandler()),
	CUSTOM(new CustomCronTagStyleHandler()),
	;
	
	private CronTagStyleHandler handler;
	
	CronTagStyleType(CronTagStyleHandler handler) {
		this.handler = handler;
	}
	
	public CronTagStyleHandler getHandler() {
		return this.handler;
	}
}

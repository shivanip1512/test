package com.cannontech.web.amr.util.cronExpressionTag;

import com.cannontech.common.i18n.DisplayableEnum;

public enum CronExprTagDailyOptionEnum implements DisplayableEnum {

	EVERYDAY,
	WEEKDAYS;
	
	private String baseKey = "yukon.common.cronExprTagDailyOptionEnum.";
	
	public String getDescription() {
		return name();
	}

	@Override
	public String getFormatKey() {
		return baseKey + name();
	}
}

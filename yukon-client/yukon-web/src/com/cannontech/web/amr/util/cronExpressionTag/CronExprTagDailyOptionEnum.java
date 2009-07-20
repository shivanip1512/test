package com.cannontech.web.amr.util.cronExpressionTag;

public enum CronExprTagDailyOptionEnum {

	EVERYDAY("Daily"),
	WEEKDAYS("Weekdays");
	
	private String description;
	
	CronExprTagDailyOptionEnum(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}

package com.cannontech.web.amr.util.cronExpressionTag;


public enum CronTagStyleType {

	DAILY(1),
	WEEKLY(2),
	MONTHLY(3),
	ONETIME(4),
	CUSTOM(5),
	;
	
	// used to concretely indicate to the CronTagStyleHandlers how they should sort themselves.
	private int order;
	
	CronTagStyleType(int order) {
	    this.order = order;
	}
	
	public int getOrder() {
        return order;
    }
}

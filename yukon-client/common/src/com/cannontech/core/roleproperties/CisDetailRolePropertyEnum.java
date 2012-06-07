package com.cannontech.core.roleproperties;

public enum CisDetailRolePropertyEnum {

	NONE(null),
	MULTISPEAK("accountInformationWidget"),
	CAYENTA("cayentaAccountInformationWidget");
	
	private String widgetName;
	
	CisDetailRolePropertyEnum(String widgetName) {
		this.widgetName = widgetName;
	}
	
	public String getWidgetName() {
		return widgetName;
	}
}

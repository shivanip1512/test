package com.cannontech.web.visualDisplays.model;

public enum HourlyDataTypeEnum {

	TODAY_INTEGRATED_HOURLY_DATA("Today Integrated Hourly Data"),
	PEAK_DAY_INTEGRATED_HOURLY_DATA("Peak Day Integrated Hourly Data"),
	TODAY_LOAD_CONTROL_PREDICATION_DATA("Today Load Control Prediction Data"),
	TOMORROW_LOAD_CONTROL_PREDICTION_DATA("Tomorrow Load Control Prediction Data");
	
	private String description;
	
	HourlyDataTypeEnum(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}

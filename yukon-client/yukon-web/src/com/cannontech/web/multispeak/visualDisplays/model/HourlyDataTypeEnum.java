package com.cannontech.web.multispeak.visualDisplays.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum HourlyDataTypeEnum implements DisplayableEnum {

	TODAY_INTEGRATED_HOURLY_DATA,
	PEAK_DAY_INTEGRATED_HOURLY_DATA,
	TODAY_LOAD_CONTROL_PREDICATION_DATA,
	TOMORROW_LOAD_CONTROL_PREDICTION_DATA;
	
	private final String keyPrefix = "yukon.web.modules.visualDisplays.hourlyDataTypeEnum.";
	
	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}

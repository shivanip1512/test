package com.cannontech.web.multispeak.visualDisplays.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum DataTypeEnum implements DisplayableEnum {
	
	CURRENT_LOAD,
	CURRENT_IH,
	LOAD_TO_PEAK,
	PEAK_IH_LOAD,
	PEAK_DAY_TIMESTAMP;
	
	private final String keyPrefix = "yukon.web.modules.visualDisplays.dataTypeEnum.";
	
	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}

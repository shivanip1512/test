package com.cannontech.amr.statusPointMonitoring.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum OutageActionType implements DisplayableEnum {
    Outage,
    Restoration,
    NoResponse,
    ;

    private String baseKey = "yukon.common.outageActionType.";
    
	@Override
	public String getFormatKey() {
		return baseKey + name();
	}
}
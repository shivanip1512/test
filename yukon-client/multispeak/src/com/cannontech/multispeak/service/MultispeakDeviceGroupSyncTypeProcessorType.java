package com.cannontech.multispeak.service;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MultispeakDeviceGroupSyncTypeProcessorType implements DisplayableEnum {

	SUBSTATION,
	BILLING_CYCLE,
	;
	
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.adminSetup.deviceGroupSync.multispeakDeviceGroupSyncTypeProcessorType." + this;
    }
}

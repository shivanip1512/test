package com.cannontech.multispeak.service;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MultispeakDeviceGroupSyncProgressStatus implements DisplayableEnum {

	RUNNING,
	FAILED,
	CANCELED,
	FINISHED;
	
	private String keyPrefix = "yukon.web.modules.multispeak.deviceGroupSync.multispeakDeviceGroupSyncProgressStatus.";
	
	@Override
	public String getFormatKey() {
		return keyPrefix + this;
	}
}

package com.cannontech.multispeak.service;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MultispeakDeviceGroupSyncStatus implements DisplayableEnum {

	IN_PROGRESS,
	IN_PROGRESS_FAILED,
	IN_PROGRESS_CANCELED,
	IN_PROGRESS_FINISHED;
	
	private String keyPrefix = "yukon.web.modules.multispeak.deviceGroupSync.multispeakDeviceGroupSyncStatus.";
	
	@Override
	public String getFormatKey() {
		return keyPrefix + this;
	}
}

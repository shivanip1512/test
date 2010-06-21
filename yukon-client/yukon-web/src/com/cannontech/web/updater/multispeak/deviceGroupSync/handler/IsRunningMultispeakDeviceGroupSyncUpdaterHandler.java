package com.cannontech.web.updater.multispeak.deviceGroupSync.handler;

import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.multispeak.deviceGroupSync.MultispeakDeviceGroupSyncUpdaterTypeEnum;

public class IsRunningMultispeakDeviceGroupSyncUpdaterHandler implements MultispeakDeviceGroupSyncUpdaterHandler {

	@Override
	public String handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext) {
		return String.valueOf(progress.isRunning());
	}

	@Override
	public MultispeakDeviceGroupSyncUpdaterTypeEnum getUpdaterType() {
		return MultispeakDeviceGroupSyncUpdaterTypeEnum.IS_RUNNING;
	}
}

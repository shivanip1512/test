package com.cannontech.web.updater.multispeak.deviceGroupSync.handler;

import com.cannontech.multispeak.service.MultispeakDeviceGroupSyncProgress;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.multispeak.deviceGroupSync.MultispeakDeviceGroupSyncUpdaterTypeEnum;

public interface MultispeakDeviceGroupSyncUpdaterHandler {

	public String handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext);
	
	public MultispeakDeviceGroupSyncUpdaterTypeEnum getUpdaterType();
}

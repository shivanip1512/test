package com.cannontech.web.updater.multispeak.deviceGroupSync.handler;

import com.cannontech.multispeak.service.v3.MultispeakDeviceGroupSyncProgress;
import com.cannontech.user.YukonUserContext;

public interface MultispeakDeviceGroupSyncUpdaterHandler {

	public Object handle(MultispeakDeviceGroupSyncProgress progress, YukonUserContext userContext);
}

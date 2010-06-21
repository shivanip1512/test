package com.cannontech.multispeak.service;

import com.cannontech.user.YukonUserContext;


public interface MultispeakDeviceGroupSyncService {

	public void init();
	public void cancel();
	public boolean isProgressAvailable();
	public void startSyncForType(MultispeakDeviceGroupSyncType type, YukonUserContext userContext);
	public MultispeakDeviceGroupSyncProgress getProgress();
}
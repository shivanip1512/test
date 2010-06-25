package com.cannontech.multispeak.service;

import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.user.YukonUserContext;


public interface MultispeakDeviceGroupSyncService {

	public void startSyncForType(MultispeakDeviceGroupSyncType type, YukonUserContext userContext);
	public MultispeakDeviceGroupSyncProgress getProgress();
	
	/**
	 * Get a map of Instants of last completed sync for each processor type.
	 */
	public Map<MultispeakDeviceGroupSyncTypeProcessorType, Instant> getLastSyncInstants();
}
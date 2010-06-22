package com.cannontech.multispeak.service;

import org.joda.time.DateTime;

import com.cannontech.user.YukonUserContext;


public interface MultispeakDeviceGroupSyncService {

	public void reset();
	public boolean isProgressAvailable();
	public void startSyncForType(MultispeakDeviceGroupSyncType type, YukonUserContext userContext);
	public MultispeakDeviceGroupSyncProgress getProgress();
	
	/**
	 * Returns date/time for the last completed substation sync.
	 * null if a substation sync has never been run to completion.
	 */
	public DateTime getLastSubstationSyncDateTime();
	
	/**
	 * Returns date/time for the last completed billing cycle sync.
	 * null if a billing cycle sync has never been run to completion.
	 */
	public DateTime getLastBillingCycleSyncDateTime();
}
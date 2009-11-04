package com.cannontech.amr.tamperFlagProcessing.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;

public interface TamperFlagMonitorService {

	public StoredDeviceGroup getTamperFlagGroup(String name);
	
	public boolean deleteTamperFlagMonitor(int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException;
	
	/**
	 * Toggles monitor enabled status. If disabled, make enabled. If disabled, make enabled.
	 * Returns new state of the monitor.
	 * @param tamperFlagMonitorId
	 * @return
	 * @throws TamperFlagMonitorNotFoundException
	 */
	public MonitorEvaluatorStatus toggleEnabled(int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException;
}

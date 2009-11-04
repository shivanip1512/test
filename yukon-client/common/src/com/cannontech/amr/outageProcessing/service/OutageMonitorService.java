package com.cannontech.amr.outageProcessing.service;

import java.util.Date;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.OutageMonitorNotFoundException;

public interface OutageMonitorService {

	public StoredDeviceGroup getOutageGroup(String name);
	
	public boolean deleteOutageMonitor(int outageMonitorId) throws OutageMonitorNotFoundException;
	
	public Date getLatestPreviousReadingDate(OutageMonitor outageMonitor);
	
	/**
	 * Toggles monitor enabled status. If disabled, make enabled. If disabled, make enabled.
	 * Returns new state of the monitor.
	 * @param outageMonitorId
	 * @return
	 * @throws OutageMonitorNotFoundException
	 */
	public MonitorEvaluatorStatus toggleEnabled(int outageMonitorId) throws OutageMonitorNotFoundException;
}

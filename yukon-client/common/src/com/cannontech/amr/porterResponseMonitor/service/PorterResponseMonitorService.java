package com.cannontech.amr.porterResponseMonitor.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.core.dao.NotFoundException;

public interface PorterResponseMonitorService {
	public boolean delete(int monitorId) throws NotFoundException;

	/**
	 * Toggles monitor enabled status. If disabled, make enabled. If disabled,
	 * make enabled. Returns new state of the monitor.
	 * 
	 * @param porterResponseProcessingMonitorId
	 * @return
	 * @throws StatusPointMonitorNotFoundException
	 */
	public MonitorEvaluatorStatus toggleEnabled(int monitorId) throws NotFoundException;
}

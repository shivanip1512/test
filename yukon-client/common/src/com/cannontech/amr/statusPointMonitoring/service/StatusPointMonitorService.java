package com.cannontech.amr.statusPointMonitoring.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.core.dao.NotFoundException;

public interface StatusPointMonitorService {

	public boolean delete(int statusPointMonitorId) throws NotFoundException;
	
	/**
	 * Toggles monitor enabled status. If disabled, make enabled. If disabled, make enabled.
	 * Returns new state of the monitor.
	 * @param statusPointMonitorId
	 * @return
	 * @throws StatusPointMonitorNotFoundException
	 */
	public MonitorEvaluatorStatus toggleEnabled(int statusPointMonitorId) throws NotFoundException;
}

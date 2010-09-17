package com.cannontech.amr.statusPointProcessing.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.core.dao.StatusPointMonitorNotFoundException;

public interface StatusPointMonitorService {

	public boolean delete(int statusPointMonitorId) throws StatusPointMonitorNotFoundException;
	
	/**
	 * Toggles monitor enabled status. If disabled, make enabled. If disabled, make enabled.
	 * Returns new state of the monitor.
	 * @param statusPointMonitorId
	 * @return
	 * @throws StatusPointMonitorNotFoundException
	 */
	public MonitorEvaluatorStatus toggleEnabled(int statusPointMonitorId) throws StatusPointMonitorNotFoundException;
}

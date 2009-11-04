package com.cannontech.common.validation.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;

public interface ValidationMonitorService {

	/**
	 * Toggles monitor enabled status. If disabled, make enabled. If disabled, make enabled.
	 * Returns new state of the monitor.
	 * @param validationMonitorId
	 * @return
	 * @throws ValidationMonitorNotFoundException
	 */
	public MonitorEvaluatorStatus toggleEnabled(int validationMonitorId) throws ValidationMonitorNotFoundException;
}

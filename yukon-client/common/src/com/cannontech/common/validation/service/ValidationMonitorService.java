package com.cannontech.common.validation.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;

public interface ValidationMonitorService {

    void create(ValidationMonitor validationMonitor);
    
    void update(ValidationMonitor validationMonitor);
    
    boolean delete(int validationMonitorId);
    /**
     * Toggles monitor enabled status. If disabled, make enabled. If disabled, make enabled. Returns new state of the monitor.
     * @throws ValidationMonitorNotFoundException
     */
    MonitorEvaluatorStatus toggleEnabled(int validationMonitorId) throws ValidationMonitorNotFoundException;
}

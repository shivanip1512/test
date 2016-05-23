package com.cannontech.amr.statusPointMonitoring.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.core.dao.NotFoundException;

public interface StatusPointMonitorService {

    void create(StatusPointMonitor statusPointMonitor);
        
    void update(StatusPointMonitor statusPointMonitor);
    
    boolean delete(int statusPointMonitorId) throws NotFoundException;
    
    /**
     * Toggles monitor enabled status. If disabled, make enabled. If disabled, make enabled. Returns new state of the monitor.
     * @throws StatusPointMonitorNotFoundException
     */
    MonitorEvaluatorStatus toggleEnabled(int statusPointMonitorId) throws NotFoundException;
}
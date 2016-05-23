package com.cannontech.amr.porterResponseMonitor.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.core.dao.NotFoundException;

public interface PorterResponseMonitorService {

    void create(PorterResponseMonitor monitor);

    void update(PorterResponseMonitor monitor);

    boolean delete(int monitorId) throws NotFoundException;

    /**
     * Toggles monitor enabled status. If disabled, make enabled. If enabled, make disabled. Returns new state of the monitor.
     * @throws StatusPointMonitorNotFoundException
     */
    MonitorEvaluatorStatus toggleEnabled(int monitorId) throws NotFoundException;
}
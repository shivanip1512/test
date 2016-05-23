package com.cannontech.amr.outageProcessing.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.OutageMonitorNotFoundException;

public interface OutageMonitorService {

    StoredDeviceGroup getOutageGroup(String name);

    void create(OutageMonitor outageMonitor);
    
    void update(OutageMonitor outageMonitor);
    
    boolean delete(int outageMonitorId) throws OutageMonitorNotFoundException;

    /**
     * Toggles monitor enabled status. If disabled, make enabled. If disabled, make enabled. Returns new state of the monitor.
     * @throws OutageMonitorNotFoundException
     */
    MonitorEvaluatorStatus toggleEnabled(int outageMonitorId) throws OutageMonitorNotFoundException;
}

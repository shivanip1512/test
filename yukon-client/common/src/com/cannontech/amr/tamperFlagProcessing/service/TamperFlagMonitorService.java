package com.cannontech.amr.tamperFlagProcessing.service;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;

public interface TamperFlagMonitorService {

    StoredDeviceGroup getTamperFlagGroup(String name);

    void create(TamperFlagMonitor tamperFlagMonitor);
    
    void update(TamperFlagMonitor tamperFlagMonitor);
    
    boolean delete(int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException;

    /**
     * Toggles monitor enabled status. If disabled, make enabled. If disabled, make enabled. Returns new state of the monitor.
     * @throws TamperFlagMonitorNotFoundException
     */
    MonitorEvaluatorStatus toggleEnabled(int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException;
}
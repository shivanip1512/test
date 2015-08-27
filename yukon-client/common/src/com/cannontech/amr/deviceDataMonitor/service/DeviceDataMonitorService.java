package com.cannontech.amr.deviceDataMonitor.service;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;

public interface DeviceDataMonitorService {
    
    /**
     * This method should be the only way a caller should save a DeviceDataMonitor. Do not directly call DeviceDataMonitorDao.save.
     * 
     * This method does a few things (in this order):
     *  1) Looks in the database for an existing monitor (using the passed in monitor.id)
     *  2) Adds the passed in monitor and the existing monitor (if it exists, null otherwise) to our
     *     asynchronous worker queue that will
     *     a) Create the violations device group if it doesn't exist
     *     b) Recalculate the "violating" devices (if need be)
     *     c) Add any found violating devices to our violation device group. More specifically, this step will
     *        ensure the violation device group contains only the devices found in step 1 above. So, if no devices
     *        are found, then this step will clear out any existing devices in this group.
     *  3) Saves our passed in monitor using DeviceDataMonitorDao.save
     *  4) Returns our saved monitor  
     */
    DeviceDataMonitor saveAndProcess(DeviceDataMonitor monitor) throws DuplicateException;
    
    /**
     * Returns a boolean indicating whether or not a worker is currently "working"
     * on calculating violations for a monitor
     */
    boolean areViolationsBeingCalculatedForMonitor(Integer monitorId);
    
    /**
     * Gets the count of the number of devices in the monitor's violations group
     */
    int getMonitorViolationCountById(int monitorId);

    /**
     * Toggles monitor enabled status. If disabled, make enabled. If enabled,
     * make disabled. Returns new state of the monitor.
     * 
     * @param MonitorId
     * @return
     * @throws NotFoundException
     */
    boolean toggleEnabled(int monitorId) throws NotFoundException;
}

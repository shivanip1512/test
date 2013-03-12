package com.cannontech.amr.deviceDataMonitor.service;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.PointValueHolder;

/**
 * This whole service is running in the service manager and all calls
 * to it are being made through Spring Remoting (which is using Active MQ)
 * see: http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/remoting.html
 * 
 * Because of this it should be noted that all objects being passed to this service
 * are copied, serialized, and sent as a message. These calls block as would any other method call.
 * 
 * The context files behind the scenes that are doing this:
 * /services/src/com/cannontech/services/server/exportedServicesContext.xml
 * /common/src/com/cannontech/remoteServicesContext.xml
 */
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
     * Asynchronously perform the following actions
     *   1) Recalculate the "violating" devices (if need be)
     *   2) Add any found violating devices to our violation device group. More specifically, this step will
     *      ensure the violation device group contains only the devices found in step 1 above. So, if no devices
     *      are found, then this step will clear out any existing devices in this group
     */
    void asyncRecalculateViolatingPaosForMonitor(DeviceDataMonitor monitor);
    
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
    
    /**
     * Returns a boolean indicating whether or not the passed in processor's LiteState.stateRawState
     * matches the passed in PointValueHolder's value 
     */
    boolean isPointValueMatch(DeviceDataMonitorProcessor processor, PointValueHolder pointValue);

    /**
     * Returns a boolean indicating whether or not a monitor's violations device group
     * should be updated given an updatedMonitor and existingMonitor.
     * 
     * All this method is doing is performing an equality comparison of the monitor's violationsDeviceGroupPath
     * 
     * Used internally in the service and in unit tests
     */
    boolean shouldUpdateViolationsGroupNameBeforeSave(DeviceDataMonitor updatedMonitor, DeviceDataMonitor existingMonitor);

    /**
     * Returns a boolean indicating whether or not a recalculation of violating devices should be
     * triggered if the passed in updatedMonitor and existingMonitor were to be saved. Used internally in the service
     * and in unit tests
     */
    boolean shouldFindViolatingPaosBeforeSave(DeviceDataMonitor updatedMonitor, DeviceDataMonitor existingMonitor);
}

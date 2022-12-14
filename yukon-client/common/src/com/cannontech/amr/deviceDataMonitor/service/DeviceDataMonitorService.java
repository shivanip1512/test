package com.cannontech.amr.deviceDataMonitor.service;

import java.util.concurrent.ExecutionException;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;

public interface DeviceDataMonitorService {
    
    /**
     * Sends messages to SM to ask if violations are being calculated. Returns "CALCULATING" if calculation is in
     * progress, "NA" if the processing didn't start yet or violations count if available.
     * 
     * @throws ExecutionException
     */
    String getViolations(Integer monitorId) throws ExecutionException;
   
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
     * This method should be the only way a caller should delete a DeviceDataMonitor. Do not directly call DeviceDataMonitorDao.deleteMonitor
     *  1) Deletes monitor from the database
     */
    void delete(DeviceDataMonitor monitor, YukonUserContext userContext);
    
    /**
     * This method should be the only way a caller should create a DeviceDataMonitor. Do not directly call DeviceDataMonitorDao.save.
     *  1) Creates a new DeviceDataMonitor
     *  2) Sends message to Service Manager
     *  3) Service Manager creates a violation group
     *  4) Service Manager adds monitor to pending for recalculation queue
     */
    DeviceDataMonitor create(DeviceDataMonitor monitor) throws DuplicateException;
    
    /**
     * This method should be the only way a caller should update a DeviceDataMonitor. Do not directly call DeviceDataMonitorDao.save.
     *  1) Updates DeviceDataMonitor
     *  2) Sends message to Service Manager
     *  3) Service Manager updates a violation group name if the monitoring group name was changed
     *  4) Service Manager adds monitor to pending for recalculation queue
     */
    DeviceDataMonitor update(DeviceDataMonitor monitor) throws DuplicateException;

    /**
     * Sends message to Service Manager to recalculate monitor
     */
    void recaclulate(DeviceDataMonitor monitor);
}

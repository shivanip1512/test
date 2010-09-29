package com.cannontech.amr.statusPointMonitoring.dao;

import java.util.List;

import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitorProcessor;
import com.cannontech.core.dao.NotFoundException;

public interface StatusPointMonitorDao {

    public void save(StatusPointMonitor statusPointMonitor);
    
	public StatusPointMonitor getStatusPointMonitorById(Integer statusPointMonitorId) throws NotFoundException;
	
	public List<StatusPointMonitorProcessor> getProcessorsByMonitorId(int statusPointMonitor);
	
	public List<StatusPointMonitor> getAllStatusPointMonitors();
	
	/**
	 * Returns true if monitor was deleted successfully, false otherwise.
	 * @param StatusPointMonitor
	 * @return
	 */
	public boolean deleteStatusPointMonitor(int statusPointMonitor);
	
	/**
     * Returns true if processor was deleted successfully, false otherwise.
     * @param StatusPointMonitorProcessor
     * @return
     */
    public boolean deleteProcessor(int statusPointMonitorProcessorId);
}
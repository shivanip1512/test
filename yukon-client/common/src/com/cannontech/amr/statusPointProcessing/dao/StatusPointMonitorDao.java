package com.cannontech.amr.statusPointProcessing.dao;

import java.util.List;

import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitorMessageProcessor;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitor;
import com.cannontech.core.dao.StatusPointMonitorMessageProcessorNotFoundException;
import com.cannontech.core.dao.StatusPointMonitorNotFoundException;

public interface StatusPointMonitorDao {

    public void save(StatusPointMonitor statusPointMonitor);
    
	public StatusPointMonitor getStatusPointMonitorById(Integer statusPointMonitorId) throws StatusPointMonitorNotFoundException;
	
	public List<StatusPointMonitorMessageProcessor> getStatusPointMonitorMessageProcessorsByStatusPointMonitorId(int statusPointMonitor) throws StatusPointMonitorMessageProcessorNotFoundException;
	
	public List<StatusPointMonitor> getAllStatusPointMonitors();
	
	/**
	 * Returns true if monitor was deleted successfully, false otherwise.
	 * @param StatusPointMonitor
	 * @return
	 */
	public boolean deleteStatusPointMonitor(int statusPointMonitor);
	
	/**
     * Returns true if processor was deleted successfully, false otherwise.
     * @param StatusPointMonitorMessageProcessor
     * @return
     */
    public boolean deleteStatusPointMonitorMessageProcessor(int statusPointMonitorMessageProcessorId);
}
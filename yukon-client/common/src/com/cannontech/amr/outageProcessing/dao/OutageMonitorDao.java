package com.cannontech.amr.outageProcessing.dao;

import java.util.List;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.core.dao.OutageMonitorNotFoundException;

public interface OutageMonitorDao {

	public void saveOrUpdate(OutageMonitor outageMonitor);
	
	public OutageMonitor getById(int outageMonitorId) throws OutageMonitorNotFoundException;
	
	public boolean processorExistsWithName(String name);
	
	public List<OutageMonitor> getAll();
	
	public int delete(int outageMonitorId);
}

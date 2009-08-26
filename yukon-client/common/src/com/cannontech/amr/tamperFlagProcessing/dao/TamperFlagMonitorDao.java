package com.cannontech.amr.tamperFlagProcessing.dao;

import java.util.List;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;

public interface TamperFlagMonitorDao {

public void saveOrUpdate(TamperFlagMonitor tamperFlagMonitor);
	
	public TamperFlagMonitor getById(int tamperFlagMonitor) throws TamperFlagMonitorNotFoundException;
	
	public boolean processorExistsWithName(String name);
	
	public List<TamperFlagMonitor> getAll();
	
	/**
	 * Returns true if monitor was deleted successfully, false otherwise.
	 * @param tamperFlagMonitor
	 * @return
	 */
	public boolean delete(int tamperFlagMonitor);
}

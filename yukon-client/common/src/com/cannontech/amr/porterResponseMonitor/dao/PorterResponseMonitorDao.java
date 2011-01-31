package com.cannontech.amr.porterResponseMonitor.dao;

import java.util.List;

import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.core.dao.NotFoundException;

public interface PorterResponseMonitorDao {

	public void save(PorterResponseMonitor monitor);

	public PorterResponseMonitor getMonitorById(Integer monitorId)
			throws NotFoundException;

	public List<PorterResponseMonitor> getAllMonitors();

	public boolean deleteMonitor(int monitorId);
}

package com.cannontech.amr.outageProcessing.service;

import java.util.Date;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.OutageMonitorNotFoundException;

public interface OutageMonitorService {

	public StoredDeviceGroup getOutageGroup(String name);
	
	public boolean deleteOutageMonitor(int outageMonitorId) throws OutageMonitorNotFoundException;
	
	public Date getLatestPreviousReadingDate(OutageMonitor outageMonitor);
}

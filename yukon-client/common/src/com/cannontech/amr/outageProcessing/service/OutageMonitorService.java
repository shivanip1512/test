package com.cannontech.amr.outageProcessing.service;

import java.util.Calendar;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.OutageMonitorNotFoundException;

public interface OutageMonitorService {

	public StoredDeviceGroup getOutageGroup(String name);
	
	public boolean deleteOutageMonitor(int outageMonitorId) throws OutageMonitorNotFoundException;
	
	public Calendar getLatestPreviousReadingDate(OutageMonitor outageMonitor);
}

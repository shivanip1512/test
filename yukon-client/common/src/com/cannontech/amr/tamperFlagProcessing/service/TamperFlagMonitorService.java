package com.cannontech.amr.tamperFlagProcessing.service;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;

public interface TamperFlagMonitorService {

	public StoredDeviceGroup getTamperFlagGroup(String name);
	
	public boolean deleteTamperFlagMonitor(int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException;
}

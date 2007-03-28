package com.cannontech.amr.deviceread.dao;

import java.util.List;

import com.cannontech.amr.deviceread.model.DeviceReadJobLog;

public interface DeviceReadJobLogDao {
	public List<DeviceReadJobLog> getAllSchedules();
	
	public String getScheduleDisplayName(DeviceReadJobLog deviceReadJobLog);
}

package com.cannontech.web.updater.statusPointMonitoring.handler;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.statusPointMonitoring.StatusPointMonitorUpdaterTypeEnum;

public class MonitoringCountStatusPointProcessingUpdaterHandler implements StatusPointProcessingUpdaterHandler {

	private StatusPointMonitorDao statusPointMonitorDao;
	private DeviceGroupService deviceGroupService;

	@Override
	public String handle(int statusPointMonitorId, YukonUserContext userContext) {

		String countStr = "N/A";
		
		try {
			
			StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
			String groupName = statusPointMonitor.getGroupName();
			
			DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
			int deviceCount = deviceGroupService.getDeviceCount(Collections.singletonList(group));
			countStr = String.valueOf(deviceCount);
			
		} catch (NotFoundException e) {
			// no monitor by that id or no device group
		}
		
		return countStr;
	}

	@Override
	public StatusPointMonitorUpdaterTypeEnum getUpdaterType() {
		return StatusPointMonitorUpdaterTypeEnum.MONITORING_COUNT;
	}
	
	@Autowired
	public void setStatusPointMonitorDao(StatusPointMonitorDao statusPointMonitorDao) {
		this.statusPointMonitorDao = statusPointMonitorDao;
	}
	
	@Autowired
	public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}
}

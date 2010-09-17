package com.cannontech.web.updater.statusPointProcessing.handler;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.statusPointProcessing.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitor;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StatusPointMonitorNotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.statusPointProcessing.StatusPointMonitorUpdaterTypeEnum;

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
			
		} catch (StatusPointMonitorNotFoundException e) {
			// no monitor by that id
		} catch (NotFoundException e) {
			// no group. that sucks, but it could totally happen at any time, don't blow up.
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

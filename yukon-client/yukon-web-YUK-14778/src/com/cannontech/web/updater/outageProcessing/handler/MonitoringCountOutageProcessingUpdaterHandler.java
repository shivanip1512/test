package com.cannontech.web.updater.outageProcessing.handler;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.outageProcessing.OutageMonitorUpdaterTypeEnum;

public class MonitoringCountOutageProcessingUpdaterHandler implements OutageProcessingUpdaterHandler {

	private OutageMonitorDao outageMonitorDao;
	private DeviceGroupService deviceGroupService;

	@Override
	public String handle(int outageMonitorId, YukonUserContext userContext) {

		String countStr = "N/A";
		
		try {
			
			OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);
			String groupName = outageMonitor.getGroupName();
			
			DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
			int deviceCount = deviceGroupService.getDeviceCount(Collections.singletonList(group));
			countStr = String.valueOf(deviceCount);
			
		} catch (OutageMonitorNotFoundException e) {
			// no monitor by that id
		} catch (NotFoundException e) {
			// no group. that sucks, but it could totally happen at any time, don't blow up.
		}
		
		return countStr;
	}

	@Override
	public OutageMonitorUpdaterTypeEnum getUpdaterType() {
		return OutageMonitorUpdaterTypeEnum.MONITORING_COUNT;
	}
	
	@Autowired
	public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
		this.outageMonitorDao = outageMonitorDao;
	}
	
	@Autowired
	public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}
}

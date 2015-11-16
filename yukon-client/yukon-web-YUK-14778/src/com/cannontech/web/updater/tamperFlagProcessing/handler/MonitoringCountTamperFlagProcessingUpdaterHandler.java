package com.cannontech.web.updater.tamperFlagProcessing.handler;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.tamperFlagProcessing.TamperFlagMonitorUpdaterTypeEnum;

public class MonitoringCountTamperFlagProcessingUpdaterHandler implements TamperFlagProcessingUpdaterHandler {

	private TamperFlagMonitorDao tamperFlagMonitorDao;
	private DeviceGroupService deviceGroupService;

	@Override
	public String handle(int tamperFlagMonitorId, YukonUserContext userContext) {

		String countStr = "N/A";
		
		try {
			
			TamperFlagMonitor tamperFlagMonitor = tamperFlagMonitorDao.getById(tamperFlagMonitorId);
			String groupName = tamperFlagMonitor.getGroupName();
			
			DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
			int deviceCount = deviceGroupService.getDeviceCount(Collections.singletonList(group));
			countStr = String.valueOf(deviceCount);
			
		} catch (TamperFlagMonitorNotFoundException e) {
			// no monitor by that id
		} catch (NotFoundException e) {
			// no group. that sucks, but it could totally happen at any time, don't blow up.
		}
		
		return countStr;
	}

	@Override
	public TamperFlagMonitorUpdaterTypeEnum getUpdaterType() {
		return TamperFlagMonitorUpdaterTypeEnum.MONITORING_COUNT;
	}
	
	@Autowired
	public void setTamperFlagMonitorDao(TamperFlagMonitorDao tamperFlagMonitorDao) {
		this.tamperFlagMonitorDao = tamperFlagMonitorDao;
	}
	
	@Autowired
	public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}
}

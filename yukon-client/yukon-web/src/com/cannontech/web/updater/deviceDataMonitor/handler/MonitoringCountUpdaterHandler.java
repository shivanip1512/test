package com.cannontech.web.updater.deviceDataMonitor.handler;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceDataMonitor.DeviceDataMonitorUpdaterTypeEnum;

public class MonitoringCountUpdaterHandler implements DeviceDataUpdaterHandler {

    private static final Logger log = YukonLogManager.getLogger(MonitoringCountUpdaterHandler.class);

	@Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
	@Autowired private DeviceGroupService deviceGroupService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

	@Override
	public String handle(int monitorId, YukonUserContext userContext) {
		try {
			DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
			String groupName = monitor.getGroupName();
			
			DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
			int deviceCount = deviceGroupService.getDeviceCount(Collections.singletonList(group));
			return String.valueOf(deviceCount);
		} catch (NotFoundException e) {
			// no monitor by that id or no device group
		    log.debug("no monitor found with id " + monitorId);
		}
		
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return messageSourceAccessor.getMessage("yukon.common.na");
	}

	@Override
	public DeviceDataMonitorUpdaterTypeEnum getUpdaterType() {
		return DeviceDataMonitorUpdaterTypeEnum.MONITORING_COUNT;
	}
}

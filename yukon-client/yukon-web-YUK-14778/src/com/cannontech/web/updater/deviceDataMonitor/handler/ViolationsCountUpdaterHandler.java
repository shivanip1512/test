package com.cannontech.web.updater.deviceDataMonitor.handler;

import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceDataMonitor.DeviceDataMonitorUpdaterTypeEnum;

public class ViolationsCountUpdaterHandler implements DeviceDataUpdaterHandler {
    
    private static final Logger log = YukonLogManager.getLogger(ViolationsCountUpdaterHandler.class);
    
    private static final String NA_MSG_KEY = "yukon.common.na";

    @Autowired private DeviceDataMonitorService deviceDataMonitorService;
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;
	@Autowired private DeviceGroupService deviceGroupService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

	@Override
	public String handle(int monitorId, YukonUserContext userContext) {
	    MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		try {
		    DeviceDataMonitor monitor = deviceDataMonitorDao.getMonitorById(monitorId);
		    
		    // if this monitor is disabled then just return "N/A"
		    if (!monitor.isEnabled()) return messageSourceAccessor.getMessage(NA_MSG_KEY);

	        boolean areViolationsBeingCalculated;
            try {
                areViolationsBeingCalculated = deviceDataMonitorService.areViolationsBeingCalculatedForMonitor(monitorId);
            } catch (ExecutionException e) {
                return messageSourceAccessor.getMessage(NA_MSG_KEY);
            }
	        if (!areViolationsBeingCalculated) {
	            int violationsCount = deviceDataMonitorService.getMonitorViolationCountById(monitorId);
	            return String.valueOf(violationsCount);
	        } else {
	            return messageSourceAccessor.getMessage("yukon.web.modules.amr.deviceDataMonitor.calculating");
	        }
			
		} catch (NotFoundException e) {
			// no monitor by that id or no device group
		    log.debug("no monitor found with id " + monitorId);
		}
        return messageSourceAccessor.getMessage(NA_MSG_KEY);
	}

	@Override
	public DeviceDataMonitorUpdaterTypeEnum getUpdaterType() {
		return DeviceDataMonitorUpdaterTypeEnum.VIOLATIONS_COUNT;
	}

    @Override
    public boolean isValueAvailableImmediately() {
        return false;
    }
}

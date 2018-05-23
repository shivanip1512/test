package com.cannontech.web.updater.deviceDataMonitor.handler;

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceDataMonitor.DeviceDataMonitorUpdaterTypeEnum;

public class ViolationsCountUpdaterHandler implements DeviceDataUpdaterHandler {
    
    private static final Logger log = YukonLogManager.getLogger(ViolationsCountUpdaterHandler.class);
    
    private static final String NA_MSG_KEY = "yukon.common.na";

    @Autowired private DeviceDataMonitorService deviceDataMonitorService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private MonitorCacheService cacheService;

    @Override
    public String handle(int monitorId, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            DeviceDataMonitor monitor = cacheService.getDeviceMonitor(monitorId);

            // if this monitor is disabled then just return "N/A"
            if (!monitor.isEnabled()) {
                return messageSourceAccessor.getMessage(NA_MSG_KEY);
            }

            try {
                String result = deviceDataMonitorService.getViolations(monitorId);
                if("CALCULATING".equals(result)) {
                    return messageSourceAccessor.getMessage("yukon.web.modules.amr.deviceDataMonitor.calculating");
                }
                if ("NA".equals(result)) {
                    return messageSourceAccessor.getMessage(NA_MSG_KEY);
                }
                return result;
            } catch (ExecutionException e) {
                log.error(e);
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

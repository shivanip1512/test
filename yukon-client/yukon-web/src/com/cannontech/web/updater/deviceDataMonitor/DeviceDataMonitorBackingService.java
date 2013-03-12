package com.cannontech.web.updater.deviceDataMonitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.deviceDataMonitor.handler.DeviceDataUpdaterHandler;

public class DeviceDataMonitorBackingService implements UpdateBackingService {

	@Autowired private List<DeviceDataUpdaterHandler> handlers;

	private Map<DeviceDataMonitorUpdaterTypeEnum, DeviceDataUpdaterHandler> handlersMap;

    @PostConstruct
    public void init() throws Exception {
        this.handlersMap = new HashMap<>();
        for (DeviceDataUpdaterHandler handler : this.handlers) {
            this.handlersMap.put(handler.getUpdaterType(), handler);
        }
    }
	
	@Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
		String[] idParts = StringUtils.split(identifier, "/");
		int monitorId = Integer.parseInt(idParts[0]);
		String updaterTypeStr = idParts[1];
		
		DeviceDataMonitorUpdaterTypeEnum updaterType = DeviceDataMonitorUpdaterTypeEnum.valueOf(updaterTypeStr);
		DeviceDataUpdaterHandler handler = handlersMap.get(updaterType);
		return handler.handle(monitorId, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
    		long afterDate, YukonUserContext userContext) {
    	return true;
    }
}


package com.cannontech.web.updater.deviceDataMonitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
	    ParsedInfo parsedInfo = new ParsedInfo(identifier);
		
        DeviceDataMonitorUpdaterTypeEnum updaterType =
            DeviceDataMonitorUpdaterTypeEnum.valueOf(parsedInfo.updaterTypeStr);
        DeviceDataUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(parsedInfo.monitorId, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
    		long afterDate, YukonUserContext userContext) {
        
        ParsedInfo parsedInfo = new ParsedInfo(fullIdentifier);
        DeviceDataMonitorUpdaterTypeEnum updaterType =
                DeviceDataMonitorUpdaterTypeEnum.valueOf(parsedInfo.updaterTypeStr);
        DeviceDataUpdaterHandler handler = handlersMap.get(updaterType);
    	return handler.isValueAvailableImmediately();
    }
    
    private class ParsedInfo {
        int monitorId;
        String updaterTypeStr;

        ParsedInfo(String identifier) {
            String[] idParts = StringUtils.split(identifier, "/");
            if(idParts[0].equals("CALCULATION_STATUS")){
                monitorId = Integer.parseInt(idParts[1]);
                updaterTypeStr = idParts[0];
            }else{
                monitorId = Integer.parseInt(idParts[0]);
                updaterTypeStr = idParts[1];  
            } 
        }
    }
}


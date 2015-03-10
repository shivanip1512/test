package com.cannontech.web.updater.deviceReconfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.deviceReconfig.handler.DeviceReconfigUpdaterHandler;

public class DeviceReconfigMonitorBackingService implements UpdateBackingService {

    private List<DeviceReconfigUpdaterHandler> handlers;
    private Map<DeviceReconfigMonitorUpdaterType, DeviceReconfigUpdaterHandler> handlersMap;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        String[] idParts = StringUtils.split(identifier, "/");
        int validationMonitorId = Integer.parseInt(idParts[0]);
        String updaterTypeStr = idParts[1];
        
        DeviceReconfigMonitorUpdaterType updaterType = DeviceReconfigMonitorUpdaterType.valueOf(updaterTypeStr);
        DeviceReconfigUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(validationMonitorId, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @PostConstruct
    public void init() throws Exception {
        
        this.handlersMap = new HashMap<DeviceReconfigMonitorUpdaterType, DeviceReconfigUpdaterHandler>();
        for (DeviceReconfigUpdaterHandler handler : this.handlers) {
            this.handlersMap.put(handler.getUpdaterType(), handler);
        }
    }
    
    @Autowired
    public void setHandlers(List<DeviceReconfigUpdaterHandler> handlers) {
        this.handlers = handlers;
    }
}
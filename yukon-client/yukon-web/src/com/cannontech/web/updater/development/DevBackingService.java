package com.cannontech.web.updater.development;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class DevBackingService implements UpdateBackingService, InitializingBean {
    
    private List<DevExecutionUpdaterHandler> handlers;
    private Map<DevExecutionUpdaterTypeEnum, DevExecutionUpdaterHandler> handlersMap;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        DevExecutionUpdaterTypeEnum updaterType = DevExecutionUpdaterTypeEnum.valueOf(identifier);
        DevExecutionUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle();
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        
        this.handlersMap = new HashMap<DevExecutionUpdaterTypeEnum, DevExecutionUpdaterHandler>();
        for (DevExecutionUpdaterHandler handler : this.handlers) {
            this.handlersMap.put(handler.getUpdaterType(), handler);
        }
    }
    
    @Autowired
    public void setHandlers(List<DevExecutionUpdaterHandler> handlers) {
        this.handlers = handlers;
    }

}
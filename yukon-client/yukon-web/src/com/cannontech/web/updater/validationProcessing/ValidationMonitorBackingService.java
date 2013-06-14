package com.cannontech.web.updater.validationProcessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.validationProcessing.handler.ValidationProcessingUpdaterHandler;

public class ValidationMonitorBackingService implements UpdateBackingService {

    private List<ValidationProcessingUpdaterHandler> handlers;
    private Map<ValidationMonitorUpdaterTypeEnum, ValidationProcessingUpdaterHandler> handlersMap;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        String[] idParts = StringUtils.split(identifier, "/");
        int validationMonitorId = Integer.parseInt(idParts[0]);
        String updaterTypeStr = idParts[1];
        
        ValidationMonitorUpdaterTypeEnum updaterType = ValidationMonitorUpdaterTypeEnum.valueOf(updaterTypeStr);
        ValidationProcessingUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(validationMonitorId, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
            long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @PostConstruct
    public void init() throws Exception {

        this.handlersMap = new HashMap<ValidationMonitorUpdaterTypeEnum, ValidationProcessingUpdaterHandler>();
        for (ValidationProcessingUpdaterHandler handler : this.handlers) {
            this.handlersMap.put(handler.getUpdaterType(), handler);
        }
    }
    
    @Autowired
    public void setHandlers(List<ValidationProcessingUpdaterHandler> handlers) {
        this.handlers = handlers;
    }
}



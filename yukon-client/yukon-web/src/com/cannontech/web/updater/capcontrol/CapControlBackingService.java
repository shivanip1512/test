package com.cannontech.web.updater.capcontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.capcontrol.handler.CapControlUpdaterHandler;

public class CapControlBackingService implements UpdateBackingService {
    
    private List<CapControlUpdaterHandler> handlers;
    private Map<CapControlUpdaterTypeEnum, CapControlUpdaterHandler> handlersMap;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        String updaterTypeStr;
        String[] idParts = StringUtils.split(identifier, "/");
        int id = 0;
        
        if(idParts.length == 1) {
            updaterTypeStr = idParts[0];
        } else {
            id = Integer.parseInt(idParts[0]);
            updaterTypeStr = idParts[1];
        }
        
        CapControlUpdaterTypeEnum updaterType = CapControlUpdaterTypeEnum.valueOf(updaterTypeStr);
        CapControlUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(id, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @PostConstruct
    public void init() throws Exception {
        
        this.handlersMap = new HashMap<CapControlUpdaterTypeEnum, CapControlUpdaterHandler>();
        for (CapControlUpdaterHandler handler : this.handlers) {
            this.handlersMap.put(handler.getUpdaterType(), handler);
        }
    }
    
    @Autowired
    public void setHandlers(List<CapControlUpdaterHandler> handlers) {
        this.handlers = handlers;
    }

}
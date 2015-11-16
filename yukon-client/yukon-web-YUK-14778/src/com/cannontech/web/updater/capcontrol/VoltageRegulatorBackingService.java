package com.cannontech.web.updater.capcontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.capcontrol.handler.VoltageRegulatorUpdaterHandler;

public class VoltageRegulatorBackingService implements UpdateBackingService {
    
    private List<VoltageRegulatorUpdaterHandler> handlers;
    private Map<VoltageRegulatorUpdaterTypeEnum, VoltageRegulatorUpdaterHandler> handlersMap;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        String[] idParts = StringUtils.split(identifier, "/");
        int id = Integer.parseInt(idParts[0]);
        String updaterTypeStr = idParts[1];
        
        VoltageRegulatorUpdaterTypeEnum updaterType = VoltageRegulatorUpdaterTypeEnum.valueOf(updaterTypeStr);
        VoltageRegulatorUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(id, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @PostConstruct
    public void init() throws Exception {
        
        this.handlersMap = new HashMap<VoltageRegulatorUpdaterTypeEnum, VoltageRegulatorUpdaterHandler>();
        for (VoltageRegulatorUpdaterHandler handler : this.handlers) {
            this.handlersMap.put(handler.getUpdaterType(), handler);
        }
    }
    
    @Autowired
    public void setHandlers(List<VoltageRegulatorUpdaterHandler> handlers) {
        this.handlers = handlers;
    }

}
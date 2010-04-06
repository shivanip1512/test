package com.cannontech.web.updater.capcontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.capcontrol.handler.LtcUpdaterHandler;

public class LtcBackingService implements UpdateBackingService, InitializingBean {
    
    private List<LtcUpdaterHandler> handlers;
    private Map<LtcUpdaterTypeEnum, LtcUpdaterHandler> handlersMap;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        String[] idParts = StringUtils.split(identifier, "/");
        int id = Integer.parseInt(idParts[0]);
        String updaterTypeStr = idParts[1];
        
        LtcUpdaterTypeEnum updaterType = LtcUpdaterTypeEnum.valueOf(updaterTypeStr);
        LtcUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(id, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        
        this.handlersMap = new HashMap<LtcUpdaterTypeEnum, LtcUpdaterHandler>();
        for (LtcUpdaterHandler handler : this.handlers) {
            this.handlersMap.put(handler.getUpdaterType(), handler);
        }
    }
    
    @Autowired
    public void setHandlers(List<LtcUpdaterHandler> handlers) {
        this.handlers = handlers;
    }

}
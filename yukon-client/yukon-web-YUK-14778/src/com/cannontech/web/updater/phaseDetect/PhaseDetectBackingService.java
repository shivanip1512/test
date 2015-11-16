package com.cannontech.web.updater.phaseDetect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.phaseDetect.PhaseDetectUpdaterTypeEnum;
import com.cannontech.web.updater.phaseDetect.handler.PhaseDetectUpdaterHandler;

public class PhaseDetectBackingService implements UpdateBackingService {
    
    private List<PhaseDetectUpdaterHandler> handlers;
    private Map<PhaseDetectUpdaterTypeEnum, PhaseDetectUpdaterHandler> handlersMap;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        String[] idParts = StringUtils.split(identifier, "/");
        int id = Integer.parseInt(idParts[0]);
        String updaterTypeStr = idParts[1];
        
        PhaseDetectUpdaterTypeEnum updaterType = PhaseDetectUpdaterTypeEnum.valueOf(updaterTypeStr);
        PhaseDetectUpdaterHandler handler = handlersMap.get(updaterType);
        return handler.handle(id, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
    
    @PostConstruct
    public void init() throws Exception {
        
        this.handlersMap = new HashMap<PhaseDetectUpdaterTypeEnum, PhaseDetectUpdaterHandler>();
        for (PhaseDetectUpdaterHandler handler : this.handlers) {
            this.handlersMap.put(handler.getUpdaterType(), handler);
        }
    }
    
    @Autowired
    public void setHandlers(List<PhaseDetectUpdaterHandler> handlers) {
        this.handlers = handlers;
    }

}

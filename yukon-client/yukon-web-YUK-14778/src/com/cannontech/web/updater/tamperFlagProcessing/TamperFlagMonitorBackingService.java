package com.cannontech.web.updater.tamperFlagProcessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.tamperFlagProcessing.handler.TamperFlagProcessingUpdaterHandler;

public class TamperFlagMonitorBackingService implements UpdateBackingService {

	private List<TamperFlagProcessingUpdaterHandler> handlers;
	private Map<TamperFlagMonitorUpdaterTypeEnum, TamperFlagProcessingUpdaterHandler> handlersMap;
	
	@Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

		String[] idParts = StringUtils.split(identifier, "/");
		int tamperFlagMonitorId = Integer.parseInt(idParts[0]);
		String updaterTypeStr = idParts[1];
		
		TamperFlagMonitorUpdaterTypeEnum updaterType = TamperFlagMonitorUpdaterTypeEnum.valueOf(updaterTypeStr);
		TamperFlagProcessingUpdaterHandler handler = handlersMap.get(updaterType);
		return handler.handle(tamperFlagMonitorId, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
    		long afterDate, YukonUserContext userContext) {
    	return true;
    }
    
    @PostConstruct
    public void init() throws Exception {

    	this.handlersMap = new HashMap<TamperFlagMonitorUpdaterTypeEnum, TamperFlagProcessingUpdaterHandler>();
    	for (TamperFlagProcessingUpdaterHandler handler : this.handlers) {
    		this.handlersMap.put(handler.getUpdaterType(), handler);
    	}
    }
    
    @Autowired
    public void setHandlers(List<TamperFlagProcessingUpdaterHandler> handlers) {
		this.handlers = handlers;
	}
}


package com.cannontech.web.updater.statusPointMonitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.statusPointMonitoring.handler.StatusPointProcessingUpdaterHandler;

public class StatusPointMonitorBackingService implements UpdateBackingService {

	private List<StatusPointProcessingUpdaterHandler> handlers;
	private Map<StatusPointMonitorUpdaterTypeEnum, StatusPointProcessingUpdaterHandler> handlersMap;
	
	@Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

		String[] idParts = StringUtils.split(identifier, "/");
		int statusPointMonitorId = Integer.parseInt(idParts[0]);
		String updaterTypeStr = idParts[1];
		
		StatusPointMonitorUpdaterTypeEnum updaterType = StatusPointMonitorUpdaterTypeEnum.valueOf(updaterTypeStr);
		StatusPointProcessingUpdaterHandler handler = handlersMap.get(updaterType);
		return handler.handle(statusPointMonitorId, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
    		long afterDate, YukonUserContext userContext) {
    	return true;
    }
    
    @PostConstruct
    public void init() throws Exception {

    	this.handlersMap = new HashMap<StatusPointMonitorUpdaterTypeEnum, StatusPointProcessingUpdaterHandler>();
    	for (StatusPointProcessingUpdaterHandler handler : this.handlers) {
    		this.handlersMap.put(handler.getUpdaterType(), handler);
    	}
    }
    
    @Autowired
    public void setHandlers(List<StatusPointProcessingUpdaterHandler> handlers) {
		this.handlers = handlers;
	}
}


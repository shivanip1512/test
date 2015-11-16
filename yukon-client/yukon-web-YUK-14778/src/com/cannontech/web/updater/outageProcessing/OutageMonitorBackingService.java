package com.cannontech.web.updater.outageProcessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.outageProcessing.handler.OutageProcessingUpdaterHandler;

public class OutageMonitorBackingService implements UpdateBackingService {

	private List<OutageProcessingUpdaterHandler> handlers;
	private Map<OutageMonitorUpdaterTypeEnum, OutageProcessingUpdaterHandler> handlersMap;
	
	@Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

		String[] idParts = StringUtils.split(identifier, "/");
		int outageMonitorId = Integer.parseInt(idParts[0]);
		String updaterTypeStr = idParts[1];
		
		OutageMonitorUpdaterTypeEnum updaterType = OutageMonitorUpdaterTypeEnum.valueOf(updaterTypeStr);
		OutageProcessingUpdaterHandler handler = handlersMap.get(updaterType);
		return handler.handle(outageMonitorId, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
    		long afterDate, YukonUserContext userContext) {
    	return true;
    }
    
    @PostConstruct
    public void init() throws Exception {

    	this.handlersMap = new HashMap<OutageMonitorUpdaterTypeEnum, OutageProcessingUpdaterHandler>();
    	for (OutageProcessingUpdaterHandler handler : this.handlers) {
    		this.handlersMap.put(handler.getUpdaterType(), handler);
    	}
    }
    
    @Autowired
    public void setHandlers(List<OutageProcessingUpdaterHandler> handlers) {
		this.handlers = handlers;
	}
}


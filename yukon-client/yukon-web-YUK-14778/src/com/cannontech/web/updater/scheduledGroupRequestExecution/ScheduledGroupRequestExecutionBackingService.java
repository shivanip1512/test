package com.cannontech.web.updater.scheduledGroupRequestExecution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;
import com.cannontech.amr.scheduledGroupRequestExecution.service.ScheduledGroupRequestExecutionBundleService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.scheduledGroupRequestExecution.handler.ScheduledGroupRequestExecutionUpdaterHandler;

public class ScheduledGroupRequestExecutionBackingService implements UpdateBackingService {

	private List<ScheduledGroupRequestExecutionUpdaterHandler> handlers;
	private Map<ScheduledGroupCommandRequestExecutionUpdaterTypeEnum, ScheduledGroupRequestExecutionUpdaterHandler> handlersMap;
	private ScheduledGroupRequestExecutionBundleService bundleService;
	
	@Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

		String[] idParts = StringUtils.split(identifier, "/");
		int id = Integer.parseInt(idParts[0]);
		String updaterTypeStr = idParts[1];
		
		ScheduledGroupCommandRequestExecutionUpdaterTypeEnum updaterType = ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.valueOf(updaterTypeStr);
		ScheduledGroupRequestExecutionUpdaterHandler handler = handlersMap.get(updaterType);
		ScheduledGroupRequestExecutionBundle execution = bundleService.getScheduledGroupRequestExecution(id);
		return handler.handle(execution, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
    	return true;
    }
    
    @PostConstruct
    public void init() throws Exception {
    	
    	this.handlersMap = new HashMap<ScheduledGroupCommandRequestExecutionUpdaterTypeEnum, ScheduledGroupRequestExecutionUpdaterHandler>();
    	for (ScheduledGroupRequestExecutionUpdaterHandler handler : this.handlers) {
    		this.handlersMap.put(handler.getUpdaterType(), handler);
    	}
    }
	
	@Autowired
	public void setHandlers(List<ScheduledGroupRequestExecutionUpdaterHandler> handlers) {
		this.handlers = handlers;
	}

	@Autowired
	public void setBundleService(ScheduledGroupRequestExecutionBundleService bundleService) {
        this.bundleService = bundleService;
    }
}

package com.cannontech.web.updater.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.job.handler.JobUpdaterHandler;

public class JobBackingService implements UpdateBackingService {

	private List<JobUpdaterHandler> handlers;
	private Map<JobUpdaterTypeEnum, JobUpdaterHandler> handlersMap;
	
	@Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

		String[] idParts = StringUtils.split(identifier, "/");
		int jobId = Integer.parseInt(idParts[0]);
		String updaterTypeStr = idParts[1];
		
		JobUpdaterTypeEnum updaterType = JobUpdaterTypeEnum.valueOf(updaterTypeStr);
		JobUpdaterHandler handler = handlersMap.get(updaterType);
		return handler.handle(jobId, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
    		long afterDate, YukonUserContext userContext) {
    	return true;
    }
    
    @PostConstruct
    public void init() throws Exception {
    	
    	this.handlersMap = new HashMap<JobUpdaterTypeEnum, JobUpdaterHandler>();
    	for (JobUpdaterHandler handler : this.handlers) {
    		this.handlersMap.put(handler.getUpdaterType(), handler);
    	}
    }
	
	@Autowired
	public void setHandlers(List<JobUpdaterHandler> handlers) {
		this.handlers = handlers;
	}
}

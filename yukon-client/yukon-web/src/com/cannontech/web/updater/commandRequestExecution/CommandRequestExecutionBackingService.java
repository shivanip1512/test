package com.cannontech.web.updater.commandRequestExecution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;
import com.cannontech.web.updater.commandRequestExecution.handler.CommandRequestExecutionUpdaterHandler;

public class CommandRequestExecutionBackingService implements UpdateBackingService {

	private List<CommandRequestExecutionUpdaterHandler> handlers;
	private Map<CommandRequestExecutionUpdaterTypeEnum, CommandRequestExecutionUpdaterHandler> handlersMap;
	
	@Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

		String[] idParts = StringUtils.split(identifier, "/");
		int id = Integer.parseInt(idParts[0]);
		String updaterTypeStr = idParts[1];
		
		CommandRequestExecutionUpdaterTypeEnum updaterType = CommandRequestExecutionUpdaterTypeEnum.valueOf(updaterTypeStr);
		CommandRequestExecutionUpdaterHandler handler = handlersMap.get(updaterType);
		return handler.handle(id, userContext);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
    	return true;
    }

    @PostConstruct
    public void init() throws Exception {
    	this.handlersMap = new HashMap<>();
    	for (CommandRequestExecutionUpdaterHandler handler : this.handlers) {
    		this.handlersMap.put(handler.getUpdaterType(), handler);
    	}
    }
	
	@Autowired
	public void setHandlers(List<CommandRequestExecutionUpdaterHandler> handlers) {
		this.handlers = handlers;
	}
	
}

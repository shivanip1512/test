package com.cannontech.web.amr.util.cronExpressionTag;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.handler.CronTagStyleHandler;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class CronExpressionTagServiceImpl implements CronExpressionTagService {
	
    private List<CronTagStyleHandler> handlers;
    private Map<CronTagStyleType, CronTagStyleHandler> handlerMap;
    
	public static final String CRONEXP_FREQ = "CRONEXP_FREQ";
	
	@Override
	public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException, IllegalArgumentException, ParseException {
		
		String freqStr = ServletRequestUtils.getRequiredStringParameter(request, id + "_" + CRONEXP_FREQ);
		
		CronTagStyleType type;
		try {
			type = CronTagStyleType.valueOf(freqStr);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(CRONEXP_FREQ + " of " + freqStr + " is not supported.");
		}
		
		CronTagStyleHandler handler = handlerMap.get(type);
		
		String expression =  handler.build(id, request, userContext);
		validateExpression(expression);
		
		return expression;
	}
	
	@Override
	public CronExpressionTagState parse(String cronExpression, YukonUserContext userContext) throws IllegalArgumentException {
		
		// blank expression => default state
		CronExpressionTagState state = new CronExpressionTagState();
		if (StringUtils.isBlank(cronExpression)) {
			return state;
		}
		
		// validate expression
		validateExpression(cronExpression);
		
		// parts
		String[] parts = getParts(cronExpression);
			
        // try to handle
    	for (CronTagStyleHandler handler : handlers) {

    		if (!handler.canParse(parts)) {
    			continue;
    		}
    		
    		state = handler.parse(parts, userContext);
    		break; 
    	}
    	
    	// not handled, return default state
    	// always set the custom expression, even if "isCustom" is not true
    	state.setCustomExpression(cronExpression);
		return state;
	}
	
	@Override
	public String getDescription(String cronExpression, YukonUserContext userContext) throws IllegalArgumentException {
		
		CronExpressionTagState cronExpressionTagState = parse(cronExpression, userContext);
		CronTagStyleType cronTagStyleType = cronExpressionTagState.getCronTagStyleType();
		
		CronTagStyleHandler cronTagStyleHandler = handlerMap.get(cronTagStyleType);
		return cronTagStyleHandler.generateDescription(cronExpressionTagState, userContext);
	}
	
	private void validateExpression(String cronExpression) throws IllegalArgumentException {
		
		try {
			new CronExpression(cronExpression);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid cron expression: " + cronExpression);
		}
	}
	
	private String[] getParts(String cronExpression) throws IllegalArgumentException {
		
		// must have at lest parts 1-6 to be valid
		String[] parts = StringUtils.split(cronExpression, " ");
		if (parts.length < 6) {
			throw new IllegalArgumentException("Incomplete expression.");
		}
		
		// add optional year part
		if (parts.length == 6) {
			
			List<String> partsList = new ArrayList<String>(Arrays.asList(parts));
			partsList.add("*");
			parts = partsList.toArray(new String[7]);
		}
		
		return parts;
	}
	
	@PostConstruct
	public void init() throws Exception {

	    Collections.sort(this.handlers);
	    
	    handlerMap = Maps.uniqueIndex(this.handlers, new Function<CronTagStyleHandler, CronTagStyleType>() {
            @Override
            public CronTagStyleType apply(CronTagStyleHandler handler) {
                return handler.getType();
            }
        });
	}
	
	@Autowired
	public void setHandlers(List<CronTagStyleHandler> handlers) {
        this.handlers = handlers;
    }
}

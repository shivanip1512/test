package com.cannontech.web.amr.util.cronExpressionTag;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.web.amr.util.cronExpressionTag.handler.CronTagStyleHandler;

public class CronExpressionTagUtils {
	
	public static final String CRONEXP_FREQ = "CRONEXP_FREQ";
	
	// BUILD
	public static String build(String id, HttpServletRequest request) throws ServletRequestBindingException, IllegalArgumentException {
		
		String freqStr = ServletRequestUtils.getRequiredStringParameter(request, id + "_" + CRONEXP_FREQ);
		
		CronTagStyleType type;
		try {
			type = CronTagStyleType.valueOf(freqStr);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(CRONEXP_FREQ + " of " + freqStr + " is not supported.");
		}
		
		CronTagStyleHandler handler = type.getHandler();
		
		return handler.build(id, request);
	}
	
	
	// PARSE
	public static CronExpressionTagState parse(String cronExpression) throws IllegalArgumentException {
		
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
		List<CronTagStyleHandler> handlers = getHandlers();
    	for (CronTagStyleHandler handler : handlers) {

    		if (!handler.canParse(parts)) {
    			continue;
    		}
    		
    		state = handler.parse(parts);
    		break; 
    	}
    	
    	// not handled, return default state
    	// always set the custom expression, even if "isCustom" is not true
    	state.setCustomExpression(cronExpression);
		return state;
	}
	
	public static String getDescription(String cronExpression) throws IllegalArgumentException {
		
		CronExpressionTagState cronExpressionTagState = parse(cronExpression);
		CronTagStyleType cronTagStyleType = cronExpressionTagState.getCronTagStyleType();
		
		CronTagStyleHandler cronTagStyleHandler = cronTagStyleType.getHandler();
		return cronTagStyleHandler.generateDescription(cronExpressionTagState);
	}
	
	private static void validateExpression(String cronExpression) throws IllegalArgumentException {
		
		try {
			new CronExpression(cronExpression);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid cron expression: " + cronExpression);
		}
	}
	
	private static String[] getParts(String cronExpression) throws IllegalArgumentException {
		
		// although 5 part expressions are legal, the tag supports features up through the 6th part
		// go ahead and assert that we only deal with 6 part expressions
		String[] parts = StringUtils.split(cronExpression, " ");
		if (parts.length < 6) {
			throw new IllegalArgumentException("Incomplete expression.");
		}
		
		return parts;
	}
	
	private static List<CronTagStyleHandler> getHandlers() {
		
		List<CronTagStyleHandler> handlers = new MappingList<CronTagStyleType, CronTagStyleHandler>(Arrays.asList(CronTagStyleType.values()), new ObjectMapper<CronTagStyleType, CronTagStyleHandler>() {
			public CronTagStyleHandler map(CronTagStyleType from) throws ObjectMappingException {
            	return from.getHandler();
            }
		});
		
		return handlers;
	}
}

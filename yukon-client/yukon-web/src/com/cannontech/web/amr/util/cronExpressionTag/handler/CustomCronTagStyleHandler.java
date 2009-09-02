package com.cannontech.web.amr.util.cronExpressionTag.handler;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public class CustomCronTagStyleHandler extends CronTagStyleHandlerBase {

	public static final String CRONEXP_CUSTOM_EXPRESSION = "CRONEXP_CUSTOM_EXPRESSION";
	
	@Override
    public CronTagStyleType getType() {
        return CronTagStyleType.CUSTOM;
    }
	
	// BUILD
	@Override
	public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException {

		String customExpression = ServletRequestUtils.getRequiredStringParameter(request, id + "_" + CRONEXP_CUSTOM_EXPRESSION);
		
		boolean isValid = CronExpression.isValidExpression(customExpression);
		if (!isValid) {
			throw new IllegalArgumentException("Invalid cron expression: " + customExpression);
		}
		
		return customExpression;
	}

	
	// CAN PARSE
	@Override
	public boolean canParse(String[] parts) {

		String cronExpression = StringUtils.join(parts, " ").trim();
		
		try {
			new CronExpression(cronExpression);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	
	// PARSE
	@Override
	public CronExpressionTagState parse(String[] parts, YukonUserContext userContext) {

		CronExpressionTagState state = new CronExpressionTagState();
		state.setCronTagStyleType(CronTagStyleType.CUSTOM);
		
		String cronExpression = StringUtils.join(parts, " ").trim();
		state.setCustomExpression(cronExpression);
		
		return state;
	}
	
	// DESCRIPTION
	public String generateDescription(CronExpressionTagState state) {
		
		String desc = "Custom, " + state.getCustomExpression();
		return desc;
	}
}

package com.cannontech.web.amr.util.cronExpressionTag.handler;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public interface CronTagStyleHandler extends Comparable<CronTagStyleHandler> {
	
	public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException, ParseException;
	
	public boolean canParse(String[] parts);
	
	public CronExpressionTagState parse(String[] parts, YukonUserContext userContext);
	
	public String generateDescription(CronExpressionTagState state, YukonUserContext userContext);
	
	public CronTagStyleType getType();
}

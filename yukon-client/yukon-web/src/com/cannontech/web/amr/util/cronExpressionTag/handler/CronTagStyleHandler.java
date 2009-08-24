package com.cannontech.web.amr.util.cronExpressionTag.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;

public interface CronTagStyleHandler {
	
	public String build(String id, HttpServletRequest request) throws ServletRequestBindingException;
	
	public boolean canParse(String[] parts);
	
	public CronExpressionTagState parse(String[] parts, YukonUserContext userContext);
	
	public String generateDescription(CronExpressionTagState state);
}

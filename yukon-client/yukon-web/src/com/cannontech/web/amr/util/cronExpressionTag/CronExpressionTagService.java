package com.cannontech.web.amr.util.cronExpressionTag;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.user.YukonUserContext;

public interface CronExpressionTagService {

    public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws CronException;
    
    public CronExpressionTagState parse(String cronExpression, YukonUserContext userContext) throws CronException;
    
    public String getDescription(String cronExpression, YukonUserContext userContext) throws CronException;
}

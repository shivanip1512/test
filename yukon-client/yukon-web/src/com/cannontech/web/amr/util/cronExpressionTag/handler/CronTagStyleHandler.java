package com.cannontech.web.amr.util.cronExpressionTag.handler;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public interface CronTagStyleHandler extends Comparable<CronTagStyleHandler> {
    
    String build(String id, HttpServletRequest request, YukonUserContext userContext) throws CronException;
    
    boolean canParse(String[] parts);
    
    CronExpressionTagState parse(String[] parts, YukonUserContext userContext);
    
    String generateDescription(CronExpressionTagState state, YukonUserContext userContext);
    
    CronTagStyleType getType();
    
}
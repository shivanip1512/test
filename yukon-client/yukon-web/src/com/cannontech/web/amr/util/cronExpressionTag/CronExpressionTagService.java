package com.cannontech.web.amr.util.cronExpressionTag;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;

import com.cannontech.user.YukonUserContext;

public interface CronExpressionTagService {

    public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws ServletRequestBindingException, IllegalArgumentException, ParseException;
    
    public CronExpressionTagState parse(String cronExpression, YukonUserContext userContext) throws IllegalArgumentException;
    
    public String getDescription(String cronExpression, YukonUserContext userContext) throws IllegalArgumentException;
}

package com.cannontech.web.amr.util.cronExpressionTag.handler;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExceptionType;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public class CustomCronTagStyleHandler extends CronTagStyleHandlerBase {

    public static final String CRONEXP_CUSTOM_EXPRESSION = "CRONEXP_CUSTOM_EXPRESSION";
    
    @Override
    public CronTagStyleType getType() {
        return CronTagStyleType.CUSTOM;
    }
    
    public static String getCustomExpression(String id, HttpServletRequest request) throws CronException {
        try {
            return ServletRequestUtils.getRequiredStringParameter(request, id + "_" + CRONEXP_CUSTOM_EXPRESSION);
        } catch (ServletRequestBindingException e) {
            throw new CronException(CronExceptionType.REQUEST_BINDING);
        }
    }
    
    @Override
    public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws CronException {
        
        String customExpression;
        try {
            customExpression = ServletRequestUtils.getRequiredStringParameter(request, 
                    id + "_" + CRONEXP_CUSTOM_EXPRESSION);
        } catch (ServletRequestBindingException e) {
            throw new CronException(CronExceptionType.REQUEST_BINDING);
        }
        
        boolean isValid = CronExpression.isValidExpression(customExpression);
        if (!isValid) {
            throw new CronException(CronExceptionType.PARSING);
        }
        
        return customExpression;
    }
    
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
    
    @Override
    public CronExpressionTagState parse(String[] parts, YukonUserContext userContext) {

        CronExpressionTagState state = new CronExpressionTagState();
        state.setCronTagStyleType(CronTagStyleType.CUSTOM);
        
        String cronExpression = StringUtils.join(parts, " ").trim();
        state.setCustomExpression(cronExpression);
        
        return state;
    }
    
    public String generateDescription(CronExpressionTagState state, YukonUserContext userContext) {
        String desc = "Custom, " + state.getCustomExpression();
        return desc;
    }
    
}
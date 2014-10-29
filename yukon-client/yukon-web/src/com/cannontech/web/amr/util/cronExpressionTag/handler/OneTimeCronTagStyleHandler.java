package com.cannontech.web.amr.util.cronExpressionTag.handler;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExceptionType;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public class OneTimeCronTagStyleHandler extends CronTagStyleHandlerBase {

    public static final String CRONEXP_ONETIME_DATE = "CRONEXP_ONETIME_DATE";
    
    @Override
    public CronTagStyleType getType() {
        return CronTagStyleType.ONETIME;
    }
    
    // BUILD
    @Override
    public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws CronException {

        String[] parts = new String[]{"*", "*", "*", "*", "*", "*", "*"};
        
        // time
        buildTime(id, request, parts);
        Calendar cal;
        try {
            String dateStr = ServletRequestUtils.getRequiredStringParameter(request, id + "_" + CRONEXP_ONETIME_DATE);
            Date date = dateFormattingService.flexibleDateParser(dateStr, userContext);
            cal = dateFormattingService.getCalendar(userContext);
            cal.clear();
            cal.setTime(date);
        } catch (ServletRequestBindingException | ParseException e) {
            if (e instanceof ServletRequestBindingException) {
                throw new CronException(CronExceptionType.REQUEST_BINDING);
            } else {
                throw new CronException(CronExceptionType.PARSING);
            }
        }
        
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        
        // one time
        parts[3] = String.valueOf(dayOfMonth);
        parts[4] = String.valueOf(month);
        parts[5] = "?";
        parts[6] = String.valueOf(year);
        
        return StringUtils.join(parts, " ").trim();
    }
    
    // CAN PARSE
    @Override
    public boolean canParse(String[] parts) {
        
        if (usesCustomTime(parts)) {
            return false;
        }
        
        if (parts.length == 7 && NumberUtils.isDigits(parts[6])) {
            return true;
        }
        
        return false;
    }
    
    // PARSE
    @Override
    public CronExpressionTagState parse(String[] parts, YukonUserContext userContext) {
        
        CronExpressionTagState state = new CronExpressionTagState();
        parseTime(parts, state);
        state.setCronTagStyleType(CronTagStyleType.ONETIME);
        
        if (NumberUtils.isDigits(parts[3]) && NumberUtils.isDigits(parts[4]) && NumberUtils.isDigits(parts[6])) {
            
            Calendar cal = dateFormattingService.getCalendar(userContext);
            cal.clear();
            cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(parts[3]));
            cal.set(Calendar.MONTH, Integer.valueOf(parts[4]) - 1);
            cal.set(Calendar.YEAR, Integer.valueOf(parts[6]));
            
            state.setDate(cal.getTime());
        }
        
        return state;
    }
    
    // DESCRIPTION
    public String generateDescription(CronExpressionTagState state, YukonUserContext userContext) {
        String dateStr = dateFormattingService.format(state.getDate(), DateFormatEnum.DATE, userContext);
        String desc = "One-time, " + dateStr + ", at " + getTimeDescription(state, userContext);
        return desc;
    }
    
}
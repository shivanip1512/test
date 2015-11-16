package com.cannontech.web.amr.util.cronExpressionTag.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExceptionType;
import com.cannontech.web.amr.util.cronExpressionTag.CronExprTagAmPmOptionEnum;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;


public abstract class CronTagStyleHandlerBase implements CronTagStyleHandler {
    
    protected DateFormattingService dateFormattingService;
    
    public static final String CRONEXP_HOUR = "CRONEXP_HOUR";
    public static final String CRONEXP_MINUTE = "CRONEXP_MINUTE";
    public static final String CRONEXP_AMPM = "CRONEXP_AMPM";
    
    @Override
    public int compareTo(CronTagStyleHandler o) {
        
        Integer thisTypeOrder = this.getType().getOrder();
        Integer otherTypeOrder = o.getType().getOrder();
        return thisTypeOrder.compareTo(otherTypeOrder);
    }
    
    // PARSE TIME
    protected void parseTime(String[] parts, CronExpressionTagState state) {
        
        // seconds
        String secondStr = parts[0];
        if (NumberUtils.isDigits(secondStr)) {
            state.setSecond(Integer.valueOf(secondStr));
        }
        
        // minutes
        String minuteStr = parts[1];
        if (NumberUtils.isDigits(minuteStr)) {
            state.setMinute(Integer.valueOf(minuteStr));
        }
        
        // hours
        String hourStr = parts[2];
        if (NumberUtils.isDigits(hourStr)) {
            
            int hour = Integer.valueOf(hourStr);
            
            if (hour == 0) {
                hour = 12;
                state.setCronExpressionAmPm(CronExprTagAmPmOptionEnum.AM);
            } else {
                
                // am pm
                if (hour >= 12) {
                    state.setCronExpressionAmPm(CronExprTagAmPmOptionEnum.PM);
                    
                    if (hour > 12) {
                        hour -= 12;
                    }
                }
            }
            
            state.setHour(hour);
        }
    }
    
    // BUILD TIME
    protected void buildTime(String id, HttpServletRequest request, String[] parts) throws CronException {
        
        int hour;
        int minute;
        String amPmStr;
        try {
            hour = ServletRequestUtils.getRequiredIntParameter(request, id + "_" + CRONEXP_HOUR);
            minute = ServletRequestUtils.getRequiredIntParameter(request, id + "_" + CRONEXP_MINUTE);
            amPmStr = ServletRequestUtils.getRequiredStringParameter(request, id + "_" + CRONEXP_AMPM);
        } catch (ServletRequestBindingException e) {
            throw new CronException(CronExceptionType.REQUEST_BINDING, e);
        }
        
        CronExprTagAmPmOptionEnum amPm = CronExprTagAmPmOptionEnum.valueOf(amPmStr);
        
        if (amPm.equals(CronExprTagAmPmOptionEnum.AM)) {
            if (hour == 12) {
                hour = 0;
            }
        } else if (amPm.equals(CronExprTagAmPmOptionEnum.PM)){
            if (hour < 12) {
                hour += 12;
            }
        } else {
            throw new CronException(CronExceptionType.PARSING);
        }
        
        parts[0] = "0";
        parts[1] = String.valueOf(minute);
        parts[2] = String.valueOf(hour);
    }
    
    // USES CUSTOM TIME
    // check if expression uses a time that can not be represented by the tag elements (needs to display as custom cron)
    protected boolean usesCustomTime(String[] parts) {
        
        // anything except 0 is custom
        String secondStr = parts[0];
        if (!NumberUtils.isDigits(secondStr)) {
            return true;
        } else {
            if (Integer.valueOf(secondStr) > 0) {
                return true;
            }
        }
        
        // hours field must not have - . Example: 0 0 15-18 ? * * (hours: 15,16,17,18)
        String hourStr = parts[2];
        if (!NumberUtils.isDigits(hourStr)) {
            return true;
        }
        
        // must be multiple of 5
        String minuteStr = parts[1];
        if (!NumberUtils.isDigits(minuteStr)) {
            return true;
        } else {
            if (Integer.valueOf(minuteStr) % 5 != 0) {
                return true;
            }
        }
                
        return false;
    }
    
    protected String getTimeDescription(CronExpressionTagState state, YukonUserContext yukonUserContext) {
        
        int hour = state.getHour();
        int min = state.getMinute();
        CronExprTagAmPmOptionEnum cronExpressionAmPm = state.getCronExpressionAmPm();
        if (cronExpressionAmPm.equals(CronExprTagAmPmOptionEnum.PM) && hour < 12) {
            hour += 12;
        } else if (cronExpressionAmPm.equals(CronExprTagAmPmOptionEnum.AM) && hour == 12) {
            hour = 0;
        }
        
        LocalTime time = new LocalTime(hour, min);
        String timeDesc = dateFormattingService.format(time, DateFormatEnum.TIME, yukonUserContext);
        
        return timeDesc;
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
}

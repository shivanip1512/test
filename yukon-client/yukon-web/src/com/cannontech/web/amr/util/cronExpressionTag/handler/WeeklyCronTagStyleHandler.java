package com.cannontech.web.amr.util.cronExpressionTag.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronDay;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExceptionType;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public class WeeklyCronTagStyleHandler extends CronTagStyleHandlerBase {

    @Override
    public CronTagStyleType getType() {
        return CronTagStyleType.WEEKLY;
    }
    
    // BUILD
    @Override
    public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws CronException {
        
        String[] parts = new String[]{"*", "*", "*", "*", "*", "*"};
        
        // time
        buildTime(id, request, parts);
        
        // weekly
        parts[3] = "?";
        parts[4] = "*";
        
        List<String> selectedDays = new ArrayList<String>();
        for (CronDay cronDay : CronDay.values()) {
            
            String dayStr = ServletRequestUtils.getStringParameter(request, id + "_" + cronDay.getRequestName(), null);
            if (dayStr != null) {
                selectedDays.add(String.valueOf(dayStr));
            }
        }
        
        if (selectedDays.size() > 0) {
            parts[5] = StringUtils.join(selectedDays, ",");
        } else {
            throw new CronException(CronExceptionType.PARSING);
        }
        
        return StringUtils.join(parts, " ").trim();
    }
    
    // CAN PARSE
    @Override
    public boolean canParse(String[] parts) {
        
        if (usesCustomTime(parts)) {
            return false;
        }
        
        String dayOfMonthStr = parts[3];
        String dayOfWeek = parts[5];
        String year = parts[6];
        String[] dayStrsArray = {"1", "2", "3", "4", "5", "6", "7"};
        List<String> dayStrsList = Arrays.asList(dayStrsArray);
        if (dayOfMonthStr.equals("?") && year.equals("*")) {
            
            // dayOfWeek must be single day number or list of day numbers
            String[] dayStrs = dayOfWeek.split(",");
            for (String dayStr : dayStrs) {
                if (!dayStrsList.contains(dayStr)) {
                    return false;
                }
            }
            return true;
        }
        
        return false;
    }
    
    // PARSE
    @Override
    public CronExpressionTagState parse(String[] parts, YukonUserContext userContext) {
        
        CronExpressionTagState state = new CronExpressionTagState();
        parseTime(parts, state);
        state.setCronTagStyleType(CronTagStyleType.WEEKLY);
        
        String dayOfWeek = parts[5];
        String[] dayStrs = dayOfWeek.split(",");
        for (String dayStr : dayStrs) {
            int dayNum = Integer.valueOf(dayStr);
            for (CronDay cronDay : CronDay.values()) {
                if (cronDay.getNumber() == dayNum) {
                    state.addSelectedCronDay(cronDay);
                }
            }
        }
        
        return state;
    }
    
    // DESCRIPTION
    public String generateDescription(CronExpressionTagState state, YukonUserContext userContext) {
        
        String desc = "";
        
        int days = 0;
        List<String> daysAbbrs = new ArrayList<String>();
        List<String> daysFulls = new ArrayList<String>();
        
        for (CronDay cronDay : state.getSelectedCronDays()) {
            days++;
            daysAbbrs.add(cronDay.getAbbreviatedName());
            daysFulls.add(cronDay.getFullName());
        }
        
        if (days == 1) {
            desc += daysFulls.get(0) + ", at " + getTimeDescription(state, userContext);
        } else if (days == 2) {
            desc += daysFulls.get(0) + " and " + daysFulls.get(1) + ", at " + getTimeDescription(state, userContext);
        } else if (days > 2) {
            
            List<String> allButLast = daysAbbrs.subList(0, daysAbbrs.size() - 1);
            String last = daysAbbrs.get(daysAbbrs.size() - 1);
            
            desc += StringUtils.join(allButLast, ", ") + " and " + last + ", at " + getTimeDescription(state, userContext);
        }
        
        return desc;
    }
    
}
package com.cannontech.web.amr.util.cronExpressionTag.handler;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.util.cronExpressionTag.CronException;
import com.cannontech.web.amr.util.cronExpressionTag.CronExceptionType;
import com.cannontech.web.amr.util.cronExpressionTag.CronExprMonthlyOptionEnum;
import com.cannontech.web.amr.util.cronExpressionTag.CronExpressionTagState;
import com.cannontech.web.amr.util.cronExpressionTag.CronTagStyleType;

public class MonthlyCronTagStyleHandler extends CronTagStyleHandlerBase {

    public static final String CRONEXP_MONTHLY_OPTION = "CRONEXP_MONTHLY_OPTION";
    public static final String CRONEXP_MONTHLY_OPTION_ON_DAY_X = "CRONEXP_MONTHLY_OPTION_ON_DAY_X";
    
    @Override
    public CronTagStyleType getType() {
        return CronTagStyleType.MONTHLY;
    }
    
    // BUILD
    @Override
    public String build(String id, HttpServletRequest request, YukonUserContext userContext) throws CronException {

        String[] parts = new String[]{"*", "*", "*", "*", "*", "*"};
        
        // time
        buildTime(id, request, parts);
        
        // monthly
        CronExprMonthlyOptionEnum monthlyOption;
        try {
            String monthlyOptionStr = ServletRequestUtils.getRequiredStringParameter(request, 
                    id + "_" + CRONEXP_MONTHLY_OPTION);
            monthlyOption = CronExprMonthlyOptionEnum.valueOf(monthlyOptionStr);
        } catch (ServletRequestBindingException e) {
            throw new CronException(CronExceptionType.REQUEST_BINDING);
        }
        
        if (monthlyOption.equals(CronExprMonthlyOptionEnum.ON_DAY)) {
            int monthlyOnDay;
            try {
                monthlyOnDay = ServletRequestUtils.getRequiredIntParameter(request, 
                        id + "_" + CRONEXP_MONTHLY_OPTION_ON_DAY_X);
            } catch (ServletRequestBindingException e) {
                throw new CronException(CronExceptionType.REQUEST_BINDING);
            }
            parts[3] = String.valueOf(monthlyOnDay);
            
        } else if (monthlyOption.equals(CronExprMonthlyOptionEnum.LAST_DAY)) {
            parts[3] = "L";
        } else {
            throw new CronException(CronExceptionType.PARSING);
        }
        
        parts[4] = "*";
        parts[5] = "?";
        
        return StringUtils.join(parts, " ").trim();
    }
    
    // CAN PARSE
    @Override
    public boolean canParse(String[] parts) {
        
        if (usesCustomTime(parts)) {
            return false;
        }
        
        String dayOfMonthStr = parts[3];
        String month = parts[4];
        String dayOfWeek = parts[5];
        String year = parts[6];
        if (month.equals("*") && dayOfWeek.equals("?") && year.equals("*") 
                && (dayOfMonthStr.equals("L") || NumberUtils.isDigits(dayOfMonthStr))) {
            return true;
        }
        
        return false;
    }
    
    // PARSE
    @Override
    public CronExpressionTagState parse(String[] parts, YukonUserContext userContext) {
        
        CronExpressionTagState state = new CronExpressionTagState();
        parseTime(parts, state);
        state.setCronTagStyleType(CronTagStyleType.MONTHLY);
        
        String dayOfMonthStr = parts[3];
        
        // last day
        if (dayOfMonthStr.equals("L")) {
            
            state.setCronExpressionMontlyOption(CronExprMonthlyOptionEnum.LAST_DAY);
        
        // on day X
        } else {
            
            state.setCronExpressionMontlyOption(CronExprMonthlyOptionEnum.ON_DAY);
            
            int dayX = Integer.valueOf(dayOfMonthStr);
            state.setCronExpressionMontlyOptionOnDayX(dayX);
        }
        
        return state;
    }
    
    // DESCRIPTION
    public String generateDescription(CronExpressionTagState state, YukonUserContext userContext) {
        
        String desc = "";
        
        CronExprMonthlyOptionEnum montlyOption = state.getCronExpressionMontlyOption();
        if (montlyOption.equals(CronExprMonthlyOptionEnum.LAST_DAY)) {
            desc += "Last day of month";
        } else if (montlyOption.equals(CronExprMonthlyOptionEnum.ON_DAY)) {
            int dayX = state.getCronExpressionMontlyOptionOnDayX();
            desc += "Monthly, on the " + dayX + CtiUtilities.getOrdinalFor(dayX);
        }
        
        desc += ", at " + getTimeDescription(state, userContext);
        
        return desc;
    }
    
}
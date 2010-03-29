package com.cannontech.web.stars.dr.operator.validator;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.validation.Errors;

import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean;

public class OptOutValidator extends SimpleValidator<OptOutBackingBean> {

    private DateFormattingService dateFormattingService;
    private RolePropertyDao rolePropertyDao;
    private YukonUserContext userContext;
    
    public OptOutValidator() {
        super(OptOutBackingBean.class);
    }
    
    public OptOutValidator(YukonUserContext userContext,
                            DateFormattingService dateFormattingService, 
                            RolePropertyDao rolePropertyDao) {

        super(OptOutBackingBean.class);
        this.userContext = userContext;
        this.dateFormattingService = dateFormattingService;
        this.rolePropertyDao = rolePropertyDao;
    }

    @Override
    public void doValidation(OptOutBackingBean optOutBackingBean, Errors errors) {

        // Opt Out Start Date
        final Date now = new Date();
        TimeZone userTimeZone = userContext.getTimeZone();
        final Date today = TimeUtil.getMidnight(now, userTimeZone);
        
        Calendar cal1 = dateFormattingService.getCalendar(userContext);
        cal1.setTime(today);
        cal1.add(Calendar.YEAR, 1);
        final Date yearFromToday = new Date(cal1.getTimeInMillis());

        
        
        
        if (optOutBackingBean.getStartDate() == null) {
            errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.invalidStartDate");
            return;
        }
        if (optOutBackingBean.getStartDate().before(today)) {
            errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.startDateTooEarly");
        }
        if (optOutBackingBean.getStartDate().after(yearFromToday)) {
            errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.startDateTooLate");
        }

        boolean optOutTodayOnly = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_OPT_OUT_TODAY_ONLY, userContext.getYukonUser());
        if(optOutTodayOnly) {
            Calendar cal2 = dateFormattingService.getCalendar(userContext);
            cal2.setTime(today);
            cal2.add(Calendar.DAY_OF_YEAR, 1);
            final Date dayFromToday = new Date(cal2.getTimeInMillis());
            
            
            if (optOutBackingBean.getStartDate().after(dayFromToday)) {
                errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.startDateToday");
            }
        }
    }

}

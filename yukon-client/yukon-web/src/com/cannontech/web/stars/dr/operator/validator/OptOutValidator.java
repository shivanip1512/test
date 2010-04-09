package com.cannontech.web.stars.dr.operator.validator;

import org.joda.time.LocalDate;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean;

public class OptOutValidator extends SimpleValidator<OptOutBackingBean> {

    private RolePropertyDao rolePropertyDao;
    private YukonUserContext userContext;
    
    public OptOutValidator() {
        super(OptOutBackingBean.class);
    }
    
    public OptOutValidator(YukonUserContext userContext,
                            RolePropertyDao rolePropertyDao) {

        super(OptOutBackingBean.class);
        this.userContext = userContext;
        this.rolePropertyDao = rolePropertyDao;
    }

    @Override
    public void doValidation(OptOutBackingBean optOutBackingBean, Errors errors) {

        // Opt Out Start Date
        final LocalDate today = new LocalDate(userContext.getJodaTimeZone());
        final LocalDate yearFromToday = today.plusYears(1);
        
        if (optOutBackingBean.getStartDate() == null) {
            errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.invalidStartDate");
            return;
        }
        if (optOutBackingBean.getStartDate().isBefore(today)) {
            errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.startDateTooEarly");
        }
        if (optOutBackingBean.getStartDate().isAfter(yearFromToday)) {
            errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.startDateTooLate");
        }

        boolean optOutTodayOnly = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_OPT_OUT_TODAY_ONLY, userContext.getYukonUser());
        if(optOutTodayOnly) {
            final LocalDate dayFromToday = today.plusDays(1);
            
            if (optOutBackingBean.getStartDate().isAfter(dayFromToday)) {
                errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.startDateToday");
            }
        }
    }

}

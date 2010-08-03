package com.cannontech.web.stars.dr.operator.validator;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventory;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean;

public class OptOutValidator extends SimpleValidator<OptOutBackingBean> {

    private boolean isOperator;
    private YukonUserContext userContext;
    private AccountInfoFragment accountInfoFragment;
    private RolePropertyDao rolePropertyDao;
    private DisplayableInventoryDao displayableInventoryDao;
    
    public OptOutValidator() {
        super(OptOutBackingBean.class);
    }
    
    public OptOutValidator(YukonUserContext userContext,
                           boolean isOperator,
                           AccountInfoFragment accountInfoFragment,
                           RolePropertyDao rolePropertyDao,
                           DisplayableInventoryDao displayableInventoryDao) {

        super(OptOutBackingBean.class);
        this.userContext = userContext;
        this.isOperator = isOperator;
        this.accountInfoFragment = accountInfoFragment;
        this.rolePropertyDao = rolePropertyDao;
        this.displayableInventoryDao = displayableInventoryDao;
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

        // Check if all opt out devices are already opted out for today.
        if (optOutBackingBean.getStartDate().equals(today)) {
            
            boolean hasADeviceAvailableForOptOut = false;
            
            List<DisplayableInventory> displayableInventories =
                displayableInventoryDao.getDisplayableInventory(accountInfoFragment.getAccountId());
            
            for (DisplayableInventory displayableInventory : displayableInventories) {
                if (!displayableInventory.isCurrentlyOptedOut()) {
                    hasADeviceAvailableForOptOut = true;
                    break;
                }
            }

            if (!hasADeviceAvailableForOptOut) {
                errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.allDevicesCurrentlyOptedOut");
            }
        }

        boolean optOutTodayOnly;
        if (isOperator) {
            optOutTodayOnly = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.OPERATOR_OPT_OUT_TODAY_ONLY, userContext.getYukonUser());
        } else {
            optOutTodayOnly = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.RESIDENTIAL_OPT_OUT_TODAY_ONLY, userContext.getYukonUser());
        }
        if(optOutTodayOnly) {
            final LocalDate dayFromToday = today.plusDays(1);
            
            if (optOutBackingBean.getStartDate().isAfter(dayFromToday)) {
                errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean.startDateToday");
            }
        }
    }

}

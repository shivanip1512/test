package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.model.OptOutBackingBean;

public class OptOutValidator extends SimpleValidator<OptOutBackingBean> {

    private boolean isOperator;
    private YukonUserContext userContext;
    private OptOutService optOutService;
    private AccountInfoFragment accountInfoFragment;
    
    public OptOutValidator() {
        super(OptOutBackingBean.class);
    }
    
    public OptOutValidator(YukonUserContext userContext,
                           boolean isOperator,
                           AccountInfoFragment accountInfoFragment,
                           OptOutService optOutService) {

        super(OptOutBackingBean.class);
        this.userContext = userContext;
        this.isOperator = isOperator;
        this.accountInfoFragment = accountInfoFragment;
        this.optOutService = optOutService;
    }

    @Override
    public void doValidation(OptOutBackingBean optOutBackingBean, Errors errors) {

        String errorCode = 
            optOutService.checkOptOutStartDate(accountInfoFragment.getAccountId(), 
                                               optOutBackingBean.getStartDate(), userContext,
                                               isOperator);
        
        if (errorCode != null) {
            errors.rejectValue("startDate", "yukon.web.modules.operator.optOutBackingBean." + errorCode);
        }
    }

}

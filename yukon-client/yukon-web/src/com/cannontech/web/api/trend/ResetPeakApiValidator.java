package com.cannontech.web.api.trend;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.trend.model.ResetPeakModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class ResetPeakApiValidator extends SimpleValidator<ResetPeakModel> {

    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public ResetPeakApiValidator() {
        super(ResetPeakModel.class);
    }

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    private MessageSourceAccessor accessor;

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    @Override
    protected void doValidation(ResetPeakModel resetPeakModel, Errors errors) {
        String dateI18nText = accessor.getMessage("yukon.common.date");
        if (!errors.hasFieldErrors("startDate")) {
            yukonApiValidationUtils.checkIsBlank(errors, "startDate", Objects.toString(resetPeakModel.getStartDate(), null),
                    dateI18nText, false);
            if (!errors.hasFieldErrors("startDate") && resetPeakModel.getStartDate().isAfterNow()) {
                errors.rejectValue("startDate", ApiErrorDetails.PAST_DATE.getCodeString());
            }
        }
    }

}

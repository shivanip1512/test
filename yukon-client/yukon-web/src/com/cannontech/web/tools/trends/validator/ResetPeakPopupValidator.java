package com.cannontech.web.tools.trends.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.ResetPeakPopupModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.api.trend.ResetPeakValidatorHelper;

public class ResetPeakPopupValidator extends SimpleValidator<ResetPeakPopupModel> {
    @Autowired private ResetPeakValidatorHelper peakValidatorHelper;

    public ResetPeakPopupValidator() {
        super(ResetPeakPopupModel.class);
    }

    @Override
    protected void doValidation(ResetPeakPopupModel resetPeakPopupModel, Errors errors) {
        peakValidatorHelper.validateIfResetPeakIsApplicable(resetPeakPopupModel.getTrendId(), errors);
        peakValidatorHelper.validateStartDate(resetPeakPopupModel.getStartDate(), errors);
    }

}

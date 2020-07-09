package com.cannontech.web.api.trend;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.ResetPeakModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.stars.util.ServletUtils;

public class ResetPeakValidator extends SimpleValidator<ResetPeakModel> {
    @Autowired private ResetPeakValidatorHelper peakValidatorHelper;

    public ResetPeakValidator() {
        super(ResetPeakModel.class);
    }

    @Override
    protected void doValidation(ResetPeakModel resetPeakModel, Errors errors) {
        String trendId = ServletUtils.getPathVariable("id");
        if (NumberUtils.isDigits(trendId)) {
            peakValidatorHelper.validateIfResetPeakIsApplicable(Integer.parseInt(trendId), errors);
            peakValidatorHelper.validateStartDate(resetPeakModel.getStartDate(), errors);
        } else {
            errors.reject("yukon.web.error.notValidNumber");
        }
    }

}

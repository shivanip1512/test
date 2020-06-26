package com.cannontech.web.api.trend;

import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class TrendCreateValidator extends SimpleValidator<TrendModel> {
    public TrendCreateValidator() {
        super(TrendModel.class);
    }

    @Override
    protected void doValidation(TrendModel trend, Errors errors) {
        // Check if name is NULL
        YukonValidationUtils.checkIfFieldRequired("name", errors, trend.getName(), "Name");
    }
}
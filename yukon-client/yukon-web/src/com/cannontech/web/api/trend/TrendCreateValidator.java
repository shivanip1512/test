package com.cannontech.web.api.trend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;

public class TrendCreateValidator extends SimpleValidator<TrendModel> {
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;
    public TrendCreateValidator() {
        super(TrendModel.class);
    }

    @Override
    protected void doValidation(TrendModel trend, Errors errors) {
        // Check if name is NULL
        yukonApiValidationUtils.checkIfFieldRequired("name", errors, trend.getName(), "Name");
    }
}
package com.cannontech.web.tools.trends.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.api.trend.TrendValidatorHelper;

@Service
public class TrendEditorValidator extends SimpleValidator<TrendModel> {
    @Autowired TrendValidatorHelper trendValidatorHelper;

    public TrendEditorValidator() {
        super(TrendModel.class);
    }

    @Override
    protected void doValidation(TrendModel trendModel, Errors errors) {
        trendValidatorHelper.validateTrendName(errors, trendModel.getName(), trendModel.getTrendId());
    }
}
package com.cannontech.web.tools.trends.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.TrendSeries;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.api.trend.TrendValidatorHelper;

@Service
public class TrendSeriesValidator extends SimpleValidator<TrendSeries> {
    @Autowired TrendValidatorHelper trendValidatorHelper;

    public TrendSeriesValidator() {
        super(TrendSeries.class);
    }

    @Override
    protected void doValidation(TrendSeries trendSeries, Errors errors) {
        trendValidatorHelper.validateTrendSeries(errors, trendSeries);
    }
}
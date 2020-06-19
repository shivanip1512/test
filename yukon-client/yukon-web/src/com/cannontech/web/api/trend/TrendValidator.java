package com.cannontech.web.api.trend;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.validator.SimpleValidator;

public class TrendValidator extends SimpleValidator<TrendModel> {
    @Autowired private TrendValidatorHelper trendValidatorHelper;

    public TrendValidator() {
        super(TrendModel.class);
    }

    @Override
    protected void doValidation(TrendModel trend, Errors errors) {

        trendValidatorHelper.validateTrendName(errors, trend.getName(), "name");

        if (CollectionUtils.isNotEmpty(trend.getTrendSeries())) {
            for (int i = 0; i < trend.getTrendSeries().size(); i++) {
                errors.pushNestedPath("trendSeries[" + i + "]");
                trendValidatorHelper.validateTrendSeries(errors, trend.getTrendSeries().get(i));
                errors.popNestedPath();
            }
        }
    }
}
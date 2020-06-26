package com.cannontech.web.api.trend;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

public class TrendUpdateValidator extends SimpleValidator<TrendModel> {
    @Autowired private IDatabaseCache dbCache;
    @Autowired private TrendValidatorHelper trendValidatorHelper;

    public TrendUpdateValidator() {
        super(TrendModel.class);
    }

    @Override
    protected void doValidation(TrendModel trend, Errors errors) {

        if (trend.getName() != null) {
            String trendId = ServletUtils.getPathVariable("id");
            LiteGraphDefinition existingTrend = dbCache.getAllGraphDefinitions()
                                                       .stream()
                                                       .filter(liteTrend -> liteTrend.getLiteID() == Integer.valueOf(trendId))
                                                       .findAny()
                                                       .orElseThrow(() -> new NotFoundException("Trend Id not found"));
            if (!existingTrend.getName().equalsIgnoreCase(trend.getName().trim())) {
                trendValidatorHelper.validateBlankName(errors, trend.getName());
                trendValidatorHelper.validateTrendName(errors, trend.getName());
                trendValidatorHelper.validateUniqueTrendName(errors, trend.getName());
            }
        }

        if (CollectionUtils.isNotEmpty(trend.getTrendSeries())) {
            for (int i = 0; i < trend.getTrendSeries().size(); i++) {
                errors.pushNestedPath("trendSeries[" + i + "]");
                trendValidatorHelper.validateTrendSeries(errors, trend.getTrendSeries().get(i));
                errors.popNestedPath();
            }
        }
    }
}
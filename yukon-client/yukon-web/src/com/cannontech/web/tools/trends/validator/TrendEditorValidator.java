package com.cannontech.web.tools.trends.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.web.api.trend.TrendValidatorHelper;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class TrendEditorValidator extends SimpleValidator<TrendModel> {
    @Autowired private IDatabaseCache dbCache;
    @Autowired TrendValidatorHelper trendValidatorHelper;

    public TrendEditorValidator() {
        super(TrendModel.class);
    }

    @Override
    protected void doValidation(TrendModel trendModel, Errors errors) {

        trendValidatorHelper.validateBlankName(errors, trendModel.getName());
        if (!errors.hasFieldErrors("name")) {
            if (trendModel.getTrendId() == null) {
                trendValidatorHelper.validateTrendName(errors, trendModel.getName());
                trendValidatorHelper.validateUniqueTrendName(errors, trendModel.getName());
            } else {
                LiteGraphDefinition existingTrend = dbCache.getAllGraphDefinitions()
                                                           .stream()
                                                           .filter(liteTrend -> liteTrend.getLiteID() == Integer.valueOf(trendModel.getTrendId()))
                                                           .findAny()
                                                           .orElseThrow(() -> new NotFoundException("Trend Id not found"));
                if (!existingTrend.getName().equalsIgnoreCase(trendModel.getName().trim())) {
                    trendValidatorHelper.validateTrendName(errors, trendModel.getName());
                    trendValidatorHelper.validateUniqueTrendName(errors, trendModel.getName());
                }
            }
        }
    }
}
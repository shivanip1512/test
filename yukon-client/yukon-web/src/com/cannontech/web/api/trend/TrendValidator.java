package com.cannontech.web.api.trend;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.RenderType;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.model.TrendSeries;
import com.cannontech.common.trend.model.TrendType.GraphType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.web.tools.points.validators.PointValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

public class TrendValidator extends SimpleValidator<TrendModel> {
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PointValidationUtil pointValidationUtil;

    private final static String basekey = "yukon.web.error.";

    public TrendValidator() {
        super(TrendModel.class);
    }

    @Override
    protected void doValidation(TrendModel trend, Errors errors) {
        //YukonValidationUtils.checkIsEmpty(errors, "name", trend.getName(), "Trend Name");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", trend.getName(), 40);
            Optional<LiteGraphDefinition> graphDefinition = dbCache.getAllGraphDefinitions().stream()
                    .filter(liteTrend -> liteTrend.getName().equals(trend.getName().trim())).findAny();
            if (graphDefinition.isPresent()) {
                errors.rejectValue("name", basekey + "nameConflict");
            }
        }
        if (CollectionUtils.isNotEmpty(trend.getTrendSeries())) {
            for (int i = 0; i < trend.getTrendSeries().size(); i++) {
                TrendSeries trendSeries = trend.getTrendSeries().get(i);
                errors.pushNestedPath("trendSeries[" + i + "]");

                //YukonValidationUtils.checkIsEmpty(errors, "pointId", Objects.toString(trendSeries.getPointId(), null), "Point ID");
                if (!errors.hasFieldErrors("pointId")) {
                    pointValidationUtil.validatePointId(errors, "pointId", trendSeries.getPointId());
                }

                //YukonValidationUtils.checkIsEmpty(errors, "label", trendSeries.getLabel(), "Label");
                if(!errors.hasFieldErrors("label")) {
                    YukonValidationUtils.checkExceedsMaxLength(errors, "label", trendSeries.getLabel(), 40);
                }

                if (trendSeries.getMultiplier() != null) {
                    YukonValidationUtils.checkIsValidDouble(errors, "multiplier", trendSeries.getMultiplier());
                }

                if (trendSeries.getStyle() != null) {
                    if (!RenderType.getWebSupportedTypes().contains(trendSeries.getStyle())) {
                        errors.rejectValue("style", basekey + "notSupported", new Object[] { trendSeries.getStyle() },
                                trendSeries.getStyle() + " is not supported");
                    }
                }
                if (trendSeries.getType() != null && trendSeries.getType() == GraphType.DATE_TYPE) {
                    //YukonValidationUtils.checkIsEmpty(errors, "date", Objects.toString(trendSeries.getDate(), null), "Date");
                    if (!errors.hasFieldErrors("date") && trendSeries.getDate().isAfterNow()) {
                        errors.rejectValue("date", basekey + "date.inThePast");
                    }
                }
                errors.popNestedPath();
            }
        }
    }
}
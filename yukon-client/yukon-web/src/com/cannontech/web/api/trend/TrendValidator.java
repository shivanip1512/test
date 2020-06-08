package com.cannontech.web.api.trend;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.trend.model.RenderType;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.model.TrendSeries;
import com.cannontech.common.trend.model.TrendType.GraphType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;

public class TrendValidator extends SimpleValidator<TrendModel> {
    @Autowired private IDatabaseCache dbCache;
    @Autowired private LMValidatorHelper helper;

    private final static String basekey = "yukon.web.error.";
    private final static ImmutableSet<RenderType> supportedTypes = ImmutableSet.of(RenderType.LINE, RenderType.BAR,
            RenderType.STEP);

    public TrendValidator() {
        super(TrendModel.class);
    }

    @Override
    protected void doValidation(TrendModel trend, Errors errors) {
        YukonValidationUtils.checkIsBlank(errors, "name", trend.getName(), false);
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

                YukonValidationUtils.checkIsBlank(errors, "pointId", Objects.toString(trendSeries.getPointId(), null), false);
                if (!errors.hasFieldErrors("pointId")) {
                    helper.validatePointId(errors, "pointId", trendSeries.getPointId());
                }

                YukonValidationUtils.checkIsBlankOrExceedsMaxLength(errors, "label", trendSeries.getLabel(), false, 40);

                if (trendSeries.getMultiplier() != null) {
                    YukonValidationUtils.checkIsValidDouble(errors, "multiplier", trendSeries.getMultiplier());
                }

                if (trendSeries.getStyle() != null) {
                    if (!supportedTypes.contains(trendSeries.getStyle())) {
                        errors.rejectValue("style", basekey + "notSupported", new Object[] { trendSeries.getStyle() },
                                trendSeries.getStyle() + " is not supported");
                    }
                }
                if (trendSeries.getType() != null && trendSeries.getType() == GraphType.DATE_TYPE) {
                    YukonValidationUtils.checkIsBlank(errors, "date", Objects.toString(trendSeries.getDate(), null), false);
                    if (!errors.hasFieldErrors("date") && !trendSeries.getDate().isBefore(LocalDate.now())) {
                        errors.rejectValue("date", basekey + "date.inThePast");
                    }
                }
                errors.popNestedPath();
            }
        }
    }
}

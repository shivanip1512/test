package com.cannontech.web.api.trend;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.trend.model.RenderType;
import com.cannontech.common.trend.model.TrendSeries;
import com.cannontech.common.trend.model.TrendType.GraphType;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.points.validators.PointValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

public class TrendValidatorHelper {
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PointValidationUtil pointValidationUtil;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private MessageSourceAccessor accessor;

    private final static String basekey = "yukon.web.error.";
    private final static String commonkey = "yukon.common.";
    private final static String tdcBasekey = "yukon.web.modules.tools.trend.";

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    /**
     * Validate Trend name.
     */
    public void validateTrendName(Errors errors, String trendName, String fieldName, Integer trendId) {
        // Applicable for update flow. We must have trendId but trendName is optional, Skip name validation when it's null.
        if (trendId != null && trendName == null) {
            return;
        }
        String nameI18nText = accessor.getMessage(commonkey + "name");

        YukonValidationUtils.checkIsBlank(errors, "name", trendName, nameI18nText, false);
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", trendName, 40);

            if (StringUtils.containsAny(trendName, PaoUtils.ILLEGAL_NAME_CHARS)) {
                errors.rejectValue("name", basekey + "paoName.containsIllegalChars");
            }

            if (trendId == null) {
                validateUniqueTrendName(errors, trendName);
            } else {
                LiteGraphDefinition existingTrend = dbCache.getAllGraphDefinitions()
                                                           .stream()
                                                           .filter(liteTrend -> liteTrend.getLiteID() == Integer.valueOf(trendId))
                                                           .findAny()
                                                           .orElseThrow(() -> new NotFoundException("Trend Id not found"));
                if (!existingTrend.getName().equalsIgnoreCase(trendName.trim()))
                    validateUniqueTrendName(errors, trendName);
            }
        }
    }

    /**
     * Check if Trend name already exists.
     */
    private void validateUniqueTrendName(Errors errors, String trendName) {
        dbCache.getAllGraphDefinitions()
               .stream()
               .filter(liteTrend -> liteTrend.getName().equalsIgnoreCase(trendName.trim()))
               .findAny()
               .ifPresent(def -> errors.rejectValue("name", basekey + "nameConflict"));
    }

    /**
     * Validate TrendSeries data.
     */
    public void validateTrendSeries(Errors errors, TrendSeries trendSeries) {
        String pointI18nText = accessor.getMessage(tdcBasekey + "point");
        String labelI18nText = accessor.getMessage(tdcBasekey + "label");
        String dateI18nText = accessor.getMessage(commonkey + "date");

        YukonValidationUtils.checkIsBlank(errors, "pointId", Objects.toString(trendSeries.getPointId(), null), pointI18nText,
                false);
        if (!errors.hasFieldErrors("pointId")) {
            pointValidationUtil.validatePointId(errors, "pointId", trendSeries.getPointId(), pointI18nText);
        }

        YukonValidationUtils.checkIsBlank(errors, "label", trendSeries.getLabel(), labelI18nText, false);
        if (!errors.hasFieldErrors("label")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "label", trendSeries.getLabel(), 40);
        }

        if (trendSeries.getMultiplier() != null) {
            YukonValidationUtils.checkIsValidDouble(errors, "multiplier", trendSeries.getMultiplier());
        }

        if (trendSeries.getStyle() != null) {
            if (!RenderType.getWebSupportedTypes().contains(trendSeries.getStyle())) {
                errors.rejectValue("style", basekey + "notSupported", new Object[] { trendSeries.getStyle() }, "");
            }
        }
        if (trendSeries.getType() != null && trendSeries.getType() == GraphType.DATE_TYPE) {
            YukonValidationUtils.checkIsBlank(errors, "date", Objects.toString(trendSeries.getDate(), null), dateI18nText,
                    false);
            if (!errors.hasFieldErrors("date") && trendSeries.getDate().isAfterNow()) {
                errors.rejectValue("date", basekey + "date.inThePast");
            }
        }
    }
}

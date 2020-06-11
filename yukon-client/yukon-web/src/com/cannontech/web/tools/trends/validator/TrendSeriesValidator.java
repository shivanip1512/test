package com.cannontech.web.tools.trends.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.trend.model.TrendSeries;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

@Service
public class TrendSeriesValidator extends SimpleValidator<TrendSeries> {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private static final String baseKey = "yukon.web.modules.tools.trend";
    private static final String mandatoryFieldMsgKey = "yukon.web.error.fieldrequired";
    
    /* The illegal characters for label are same as that in PaoUtils.ILLEGAL_NAME_CHARS. Expect / character. This character is allowed in label. */
    public final static char[] ILLEGAL_LABEL_CHARS = { '\'', ',', '|', '"', '\\' };

    public TrendSeriesValidator() {
        super(TrendSeries.class);
    }

    @Override
    protected void doValidation(TrendSeries trendSeries, Errors errors) {

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);

        if (trendSeries.getPointId() == null) {
            errors.rejectValue("pointId", mandatoryFieldMsgKey,
                    new Object[] { accessor.getMessage("yukon.common.point") }, "Point is required.");
        }

        String label = StringUtils.trim(trendSeries.getLabel());
        if (StringUtils.isBlank(label)) {
            errors.rejectValue("label", mandatoryFieldMsgKey,
                    new Object[] { accessor.getMessage(baseKey + ".label") }, "Label is required.");
        }

        if (!errors.hasFieldErrors("label") && StringUtils.length(label) > 40) {
            errors.rejectValue("label", baseKey + ".field.error.maxLengthExceeded",
                    new Object[] { accessor.getMessage(baseKey + ".label"), 40 }, "Label cannot exceed 40 characters.");
        }

        if (!errors.hasFieldErrors("label") && !StringUtils.containsNone(label, ILLEGAL_LABEL_CHARS)) {
            errors.rejectValue("label", baseKey + ".field.error.containsIllegalChars",
                    new Object[] { accessor.getMessage(baseKey + ".label"),
                            String.valueOf(ILLEGAL_LABEL_CHARS) },
                    "Name cannot include any of the following characters: " + String.valueOf(ILLEGAL_LABEL_CHARS));
        }
        
        if (!errors.hasFieldErrors("multiplier") && trendSeries.getMultiplier() == null) {
            errors.rejectValue("multiplier", mandatoryFieldMsgKey,
                    new Object[] { accessor.getMessage(baseKey + ".multiplier") }, "Multiplier is required.");
        }
    }
}
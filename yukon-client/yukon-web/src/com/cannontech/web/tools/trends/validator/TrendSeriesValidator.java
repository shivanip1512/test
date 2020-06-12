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
import com.cannontech.web.tools.trends.helper.TrendEditorHelper;

@Service
public class TrendSeriesValidator extends SimpleValidator<TrendSeries> {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private static final String baseKey = "yukon.web.modules.tools.trend";
    private static final String mandatoryFieldMsgKey = "yukon.web.error.fieldrequired";
    
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

        //TODO: This code will be removed after, and a method from YukonValidationUtils will be added YUK-22272 is merged in master. 
        String label = StringUtils.trim(trendSeries.getLabel());
        if (StringUtils.isBlank(label)) {
            errors.rejectValue("label", mandatoryFieldMsgKey,
                    new Object[] { accessor.getMessage(baseKey + ".label") }, "Label is required.");
        }

        if (!errors.hasFieldErrors("label") && StringUtils.length(label) > 40) {
            errors.rejectValue("label", baseKey + ".field.error.maxLengthExceeded",
                    new Object[] { accessor.getMessage(baseKey + ".label"), 40 }, "Label cannot exceed 40 characters.");
        }

        if (!errors.hasFieldErrors("multiplier") && trendSeries.getMultiplier() == null) {
            errors.rejectValue("multiplier", mandatoryFieldMsgKey,
                    new Object[] { accessor.getMessage(baseKey + ".multiplier") }, "Multiplier is required.");
        }
        
        if (TrendEditorHelper.isDateType(trendSeries.getType())) {
            //TODO: This code will be removed after, and a method from YukonValidationUtils will be added YUK-22272 is merged in master. 
            if (trendSeries.getDate() == null) {
                errors.rejectValue("date", mandatoryFieldMsgKey,
                        new Object[] { accessor.getMessage("yukon.common.date") }, "Date is required.");
            } else if (trendSeries.getDate().isAfterNow()) {
                errors.rejectValue("date", baseKey + ".date.error.futureDate", "Date cannot be future date.");
            }
        }
        
    }
}
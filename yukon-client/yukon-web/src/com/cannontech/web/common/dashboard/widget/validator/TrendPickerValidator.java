package com.cannontech.web.common.dashboard.widget.validator;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.core.dao.GraphDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.common.dashboard.exception.WidgetMissingParameterException;
import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.WidgetInputType;

public class TrendPickerValidator implements WidgetInputValidator {

    private static final TrendPickerValidator singleton = new TrendPickerValidator();
    private GraphDao graphDao = YukonSpringHook.getBean("graphDao", GraphDao.class);

    private TrendPickerValidator() {
    }

    public static TrendPickerValidator get() {
        return singleton;
    }

    @Override
    public void validate(String inputName, Object inputValue, WidgetInputType type)
            throws WidgetParameterValidationException, WidgetMissingParameterException {
        String message = "Valid trend selection is required.";
        if (!StringUtils.isNumeric((String) inputValue)) {
            throw new WidgetParameterValidationException(message, inputName, "trendId.required", inputName);
        }
        Integer trendId = Integer.parseInt((String) inputValue);
        LiteGraphDefinition liteGraphDefinition = graphDao.getLiteGraphDefinition(trendId);
        if (liteGraphDefinition == null) {
            throw new WidgetParameterValidationException(message, inputName, "trendId.required", inputName);
        }
    }

}

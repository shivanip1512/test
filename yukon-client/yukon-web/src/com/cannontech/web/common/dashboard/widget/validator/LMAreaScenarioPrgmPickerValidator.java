package com.cannontech.web.common.dashboard.widget.validator;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.common.dashboard.exception.WidgetMissingParameterException;
import com.cannontech.web.common.dashboard.exception.WidgetParameterValidationException;
import com.cannontech.web.common.dashboard.model.WidgetInputType;
import com.cannontech.yukon.IDatabaseCache;

public class LMAreaScenarioPrgmPickerValidator implements WidgetInputValidator {

    private static final LMAreaScenarioPrgmPickerValidator singleton = new LMAreaScenarioPrgmPickerValidator();
    private IDatabaseCache cache = YukonSpringHook.getBean("databaseCache", IDatabaseCache.class);

    private LMAreaScenarioPrgmPickerValidator() {
    }

    public static LMAreaScenarioPrgmPickerValidator get() {
        return singleton;
    }

    @Override
    public void validate(String inputName, Object inputValue, WidgetInputType type)
            throws WidgetParameterValidationException, WidgetMissingParameterException {
        String message = "Valid Control area, control program or secnario selection is required.";
        if (!StringUtils.isNumeric((String)inputValue)) {
            throw new WidgetParameterValidationException(message, inputName, "lmSelection.required", inputName);
        }
        Integer paoId = Integer.parseInt((String)inputValue);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        if (pao.getPaoType().getPaoClass() != PaoClass.LOADMANAGEMENT) {
            throw new WidgetParameterValidationException(message, inputName, "lmSelection.required", inputName);
        }
    }

}

package com.cannontech.web.tools.dataViewer;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class TdcCustomDisplayValidator extends SimpleValidator<Display> {
    
    public TdcCustomDisplayValidator() {
        super(Display.class);
    }

    @Override
    public void doValidation(Display display, Errors errors) {
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", display.getName(), 40);
        YukonValidationUtils.checkExceedsMaxLength(errors, "description", display.getDescription(), 200);

    }

}

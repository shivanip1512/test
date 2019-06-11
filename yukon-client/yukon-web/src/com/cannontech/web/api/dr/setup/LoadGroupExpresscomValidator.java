package com.cannontech.web.api.dr.setup;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupExpresscom;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupExpresscomValidator extends LoadGroupSetupValidator<LoadGroupExpresscom> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    public LoadGroupExpresscomValidator() {
        super(LoadGroupExpresscom.class);
    }

    
    @Override
    protected void doValidation(LoadGroupExpresscom loadGroup, Errors errors) {
        super.doValidation(loadGroup, errors);
        // Address Usage
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressUsage", key + "type.required");

    }
}

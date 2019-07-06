package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupItron;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupItronValidator extends LoadGroupSetupValidator<LoadGroupItron> {

    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    @Autowired private LMValidatorHelper lmValidatorHelper;
    public LoadGroupItronValidator() {
        super(LoadGroupItron.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupItron.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupItron loadGroup, Errors errors) {

        // Validation for virtual RelayId field.

        lmValidatorHelper.checkIfEmptyOrWhitespaceName("virtualRelayId", errors, "Virtual RelayId ");
        if (!errors.hasFieldErrors("virtualRelayId")) {
            YukonValidationUtils.checkIsPositiveInt(errors, "virtualRelayId", loadGroup.getVirtualRelayId());
        }
        if (!errors.hasFieldErrors("virtualRelayId")) {
            YukonValidationUtils.checkRange(errors, "virtualRelayId", loadGroup.getVirtualRelayId(), 1, 8, true);
        }

    }
}

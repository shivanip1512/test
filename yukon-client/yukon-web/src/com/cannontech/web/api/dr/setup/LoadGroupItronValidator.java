package com.cannontech.web.api.dr.setup;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupItron;
import com.cannontech.common.validator.YukonApiValidationUtils;

@Service
public class LoadGroupItronValidator extends LoadGroupSetupValidator<LoadGroupItron> {

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

        YukonApiValidationUtils.checkIfFieldRequired("virtualRelayId", errors, loadGroup.getVirtualRelayId(),
            "Virtual RelayId ");

        if (!errors.hasFieldErrors("virtualRelayId")) {
            YukonApiValidationUtils.checkRange(errors, "virtualRelayId", loadGroup.getVirtualRelayId(), 1, 8, true);
        }

    }
}       

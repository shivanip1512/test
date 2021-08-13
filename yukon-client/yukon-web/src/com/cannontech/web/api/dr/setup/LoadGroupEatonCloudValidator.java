package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupEatonCloud;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class LoadGroupEatonCloudValidator extends LoadGroupSetupValidator<LoadGroupEatonCloud> {

    @Autowired private LMValidatorHelper lmValidatorHelper;
    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";

    public LoadGroupEatonCloudValidator() {
        super(LoadGroupEatonCloud.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupEatonCloud.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupEatonCloud loadGroup, Errors errors) {
        // Validation for virtual RelayId field.

        lmValidatorHelper.checkIfFieldRequired("virtualRelayId", errors, loadGroup.getVirtualRelayId(),
            "Virtual RelayId ");

        if (!errors.hasFieldErrors("virtualRelayId")) {
            YukonValidationUtils.checkRange(errors, "virtualRelayId", loadGroup.getVirtualRelayId(), 1, 8, true);
        }
    }
}

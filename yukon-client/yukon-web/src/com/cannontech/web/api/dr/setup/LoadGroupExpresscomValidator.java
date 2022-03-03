package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupExpresscom;

@Service
public class LoadGroupExpresscomValidator extends LoadGroupSetupValidator<LoadGroupExpresscom> {
    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;

    public LoadGroupExpresscomValidator() {
        super(LoadGroupExpresscom.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return LoadGroupExpresscom.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(LoadGroupExpresscom loadGroup, Errors errors) {
        // Route ID is mandatory for expresscom and should exists
        lmApiValidatorHelper.validateRoute(errors, loadGroup.getRouteId());
    }
}

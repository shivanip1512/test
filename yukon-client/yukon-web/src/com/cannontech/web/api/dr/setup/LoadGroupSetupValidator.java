package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class LoadGroupSetupValidator<T extends LoadGroupBase> extends SimpleValidator<T> {

    @Autowired private LMValidatorHelper lmValidatorHelper;

    public LoadGroupSetupValidator() {
        super((Class<T>) LoadGroupBase.class);
    }
    
    public LoadGroupSetupValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T loadGroup, Errors errors) {
        // Type 
        lmValidatorHelper.checkIfEmptyPaoType(errors);
        // Group Name
        lmValidatorHelper.validateNewPaoName(loadGroup.getName(), loadGroup.getType(), errors, "Group Name");
        // kWCapacity
        lmValidatorHelper.checkIfFieldRequired("kWCapacity", errors, loadGroup.getkWCapacity(), "kW Capacity");
        if (!errors.hasFieldErrors("kWCapacity")) {
            YukonValidationUtils.checkRange(errors, "kWCapacity", loadGroup.getkWCapacity(), 0.0, 99999.999, true);
        }
    }

}

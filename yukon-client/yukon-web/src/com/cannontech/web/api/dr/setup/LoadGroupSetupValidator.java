package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;

public class LoadGroupSetupValidator<T extends LoadGroupBase> extends SimpleValidator<T> {


    private final static String key = "yukon.web.modules.dr.setup.loadGroup.error.";
    @Autowired
    LMApiValidatorHelper lmApiValidatorHelper;
    public LoadGroupSetupValidator() {
        super((Class<T>) LoadGroupBase.class);
    }
    
    public LoadGroupSetupValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T loadGroup, Errors errors) {
        // Type 
        YukonApiValidationUtils.checkIfFieldRequired("type", errors, loadGroup.getType(), "Type");
        // Group Name
        lmApiValidatorHelper.validateNewPaoName(loadGroup.getName(), loadGroup.getType(), errors, "Name");
        // kWCapacity
        YukonApiValidationUtils.checkIfFieldRequired("kWCapacity", errors, loadGroup.getkWCapacity(), "kW Capacity");
        if (!errors.hasFieldErrors("kWCapacity")) {
            YukonApiValidationUtils.checkRange(errors, "kWCapacity", loadGroup.getkWCapacity(), 0.0, 99999.999, true);
        }
        if (loadGroup.getType() == PaoType.MACRO_GROUP) {
            errors.rejectValue("type", key + "type.invalid", new Object[] { "load group" }, "");
        }
    }

}

package com.cannontech.web.api.picker;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class PickerIdSearchApiValidator extends SimpleValidator<PickerIdSearchCriteria> {

   
    public PickerIdSearchApiValidator(Class<PickerIdSearchCriteria> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(PickerIdSearchCriteria idSearchCriteria, Errors errors) {
        YukonValidationUtils.checkIfFieldRequired("type", errors, idSearchCriteria.getType(), "type");

        // Check if name is NULL
        YukonValidationUtils.checkIfFieldRequired("getExtraArgs", errors, idSearchCriteria.getExtraArgs(), "getExtraArgs");

        // Check if baudRate is NULL
        YukonValidationUtils.checkIfFieldRequired("getInitialIds", errors, idSearchCriteria.getInitialIds(), "getInitialIds");
        
    }
    
   
}
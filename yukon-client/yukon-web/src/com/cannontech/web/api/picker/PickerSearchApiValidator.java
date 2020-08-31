package com.cannontech.web.api.picker;


import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class PickerSearchApiValidator extends SimpleValidator<PickerSearchCriteria> {

   
    public PickerSearchApiValidator(Class<PickerSearchCriteria> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(PickerSearchCriteria searchCriteria, Errors errors) {
      
        YukonValidationUtils.checkIfFieldRequired("type", errors, searchCriteria.getCount(), "type");

        // Check if name is NULL
        YukonValidationUtils.checkIfFieldRequired("getExtraArgs", errors, searchCriteria.getExtraArgs(), "getExtraArgs");

        // Check if baudRate is NULL
        YukonValidationUtils.checkIfFieldRequired("getInitialIds", errors, searchCriteria.getQueryString(), "getInitialIds");
        
        // Check if baudRate is NULL
        YukonValidationUtils.checkIfFieldRequired("getInitialIds", errors, searchCriteria.getStartCount(), "getInitialIds");
        
        // Check if baudRate is NULL
        YukonValidationUtils.checkIfFieldRequired("getInitialIds", errors, searchCriteria.getType(), "getInitialIds");
        
    }
    
    
}
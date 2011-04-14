package com.cannontech.web.stars.dr.operator.inventoryOperations;

import java.util.List;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterModel;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.RuleModel;

public class FilterModelValidator extends SimpleValidator<FilterModel> {
    
    public FilterModelValidator() {
        super(FilterModel.class);
    }

    @Override
    protected void doValidation(FilterModel target, Errors errors) {
        List<RuleModel> filterRules = target.getFilterRules();
        for (int i = 0; i < filterRules.size(); i++) {
            RuleModel rule = filterRules.get(i); 
                
            switch (rule.getRuleType()) {
            
                case DEVICE_STATUS_DATE_RANGE:
                    if (rule.getDeviceStateDateFrom().isAfter(rule.getDeviceStateDateTo())) {
                        YukonValidationUtils.rejectValues(errors,
                                                          "yukon.web.modules.operator.filterSelection.error.invalidDeviceStatusDateRange",
                                                          "filterRules["+i+"].deviceStateDateFrom",
                                                          "filterRules["+i+"].deviceStateDateTo");
                    }
                    break;
            
                case POSTAL_CODE:
                
                    YukonValidationUtils.checkExceedsMaxLength(errors, "filterRules["+i+"].postalCode", rule.getPostalCode(), 12);
                    break;
                
                case SERIAL_NUMBER_RANGE:
                    
                    if (rule.getSerialNumberFrom() < 0 || rule.getSerialNumberTo() < 0) {
                        errors.reject("yukon.web.modules.operator.filterSelection.error.serialNumberRangeLtZero");
                    }
                    
                    if (rule.getSerialNumberFrom() > rule.getSerialNumberTo()) {
                        errors.reject("yukon.web.modules.operator.filterSelection.error.invalidSerialNumberRange");
                    }
                    break;
                    
                    
            }
        }
    }
    
}
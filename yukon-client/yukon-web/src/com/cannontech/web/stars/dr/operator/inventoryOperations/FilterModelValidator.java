package com.cannontech.web.stars.dr.operator.inventoryOperations;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterModel;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterRuleType;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.RuleModel;

public class FilterModelValidator extends SimpleValidator<FilterModel> {
    
    public FilterModelValidator() {
        super(FilterModel.class);
    }

    @Override
    protected void doValidation(FilterModel target, Errors errors) {
        for (RuleModel rule : target.getFilterRules()) {
            if (rule.getRuleType() == FilterRuleType.SERIAL_NUMBER_RANGE) {
                if (rule.getSerialNumberFrom() < 0 || rule.getSerialNumberTo() < 0) {
                    errors.reject("yukon.web.modules.operator.filterSelection.error.serialNumberRangeLtZero");
                }

                if (rule.getSerialNumberFrom() > rule.getSerialNumberTo()) {
                    errors.reject("yukon.web.modules.operator.filterSelection.error.invalidSerialNumberRange");
                }
            }
        }
    }
    
}
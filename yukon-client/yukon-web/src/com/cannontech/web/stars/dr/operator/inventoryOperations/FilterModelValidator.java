package com.cannontech.web.stars.dr.operator.inventoryOperations;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.user.SystemUserContext;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.FilterModel;
import com.cannontech.web.stars.dr.operator.inventoryOperations.model.RuleModel;

public class FilterModelValidator extends SimpleValidator<FilterModel> {
    
    private DateFormattingService dateFormattingService;
    
    public FilterModelValidator() {
        super(FilterModel.class);
    }

    @Override
    protected void doValidation(FilterModel target, Errors errors) {
        
        for(RuleModel rule : target.getFilterRules()) {
            
            switch (rule.getRuleType()) {

            case FIELD_INSTALL_DATE:
                if (StringUtils.isBlank(rule.getFieldInstallDate())) {
                    errors.reject("yukon.web.modules.operator.filterSelection.error.invalidFieldInstallDate");
                } else {
                    try {
                        dateFormattingService.flexibleDateParser(rule.getFieldInstallDate(), new SystemUserContext());
                    } catch (ParseException e) {
                        errors.reject("yukon.web.modules.operator.filterSelection.error.invalidFieldInstallDate");
                    }
                }
                break;

            case PROGRAM_SIGNUP_DATE:
                if (StringUtils.isBlank(rule.getProgramSignupDate())) {
                    errors.reject("yukon.web.modules.operator.filterSelection.error.invalidProgramSignupDate");
                } else {
                    try {
                        dateFormattingService.flexibleDateParser(rule.getProgramSignupDate(), new SystemUserContext());
                    } catch (ParseException e) {
                        errors.reject("yukon.web.modules.operator.filterSelection.error.invalidProgramSignupDate");
                    }
                }
                break;
            
            case SERIAL_NUMBER_RANGE:
                try {
                    Integer from = Integer.parseInt(rule.getSerialNumberFrom());
                    Integer to = Integer.parseInt(rule.getSerialNumberTo());
                    
                    if(from > to) {
                        errors.reject("yukon.web.modules.operator.filterSelection.error.invalidSerialNumberRange");
                    }
                    
                } catch (NumberFormatException e) {
                    errors.reject("yukon.web.modules.operator.filterSelection.error.invalidSerialNumberRange");
                }
                break;

            default:
                break;
            }
        }
        
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
}
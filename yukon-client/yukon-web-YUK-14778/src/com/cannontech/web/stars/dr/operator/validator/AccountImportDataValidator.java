package com.cannontech.web.stars.dr.operator.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.util.Validator;
import com.cannontech.web.stars.dr.operator.model.AccountImportData;

public class AccountImportDataValidator extends SimpleValidator<AccountImportData> {
    
    public AccountImportDataValidator() {
        super(AccountImportData.class);
    }

    @Override
    public void doValidation(AccountImportData accountImportData, Errors errors) {
        
        /* Serial Number */
        if (StringUtils.isNotBlank(accountImportData.getEmail()) && !Validator.isEmailAddress(accountImportData.getEmail())) {
            errors.rejectValue("email", "yukon.web.modules.operator.accountImport.error.invalidEmail");
        }
        
    }
}
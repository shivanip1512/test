package com.cannontech.web.stars.dr.operator.general.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.web.stars.dr.operator.general.OperatorGeneralAccountController.AccountGeneral;
import com.cannontech.web.stars.dr.operator.general.model.OperatorGeneralUiExtras;

public class AccountGeneralValidator implements Validator {

	private PhoneNumberFormattingService phoneNumberFormattingService;
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return AccountGeneral.class.isAssignableFrom(clazz); 
	}

	@Override
	public void validate(Object target, Errors errors) {

		AccountGeneral accountGeneral = (AccountGeneral)target;
		AccountDto accountDto = accountGeneral.getAccountDto();
		OperatorGeneralUiExtras operatorGeneralUiExtras = accountGeneral.getOperatorGeneralUiExtras();
		
		// account number
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountDto.accountNumber", "yukon.web.modules.operator.accountGeneral.accountDto.accountNumber.accountNumberRequired");
		
		// home phone number
		if (phoneNumberFormattingService.isHasInvalidCharacters(accountDto.getHomePhone())) {
			errors.rejectValue("accountDto.homePhone", "yukon.web.modules.operator.accountGeneral.accountDto.homePhone.invalid");
		}
		
		// work phone number
		if (phoneNumberFormattingService.isHasInvalidCharacters(accountDto.getWorkPhone())) {
			errors.rejectValue("accountDto.workPhone", "yukon.web.modules.operator.accountGeneral.accountDto.workPhone.invalid");
		}
	}

	@Autowired
	public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}
}

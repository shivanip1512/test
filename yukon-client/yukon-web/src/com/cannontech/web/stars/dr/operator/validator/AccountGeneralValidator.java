package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.AddressValidator;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.web.stars.dr.operator.OperatorAccountController.AccountGeneral;

public class AccountGeneralValidator extends SimpleValidator<AccountGeneral> {

	private PhoneNumberFormattingService phoneNumberFormattingService;
	
	public AccountGeneralValidator() {
		super(AccountGeneral.class);
	}

	@Override
	public void doValidation(AccountGeneral accountGeneral, Errors errors) {

		AccountDto accountDto = accountGeneral.getAccountDto();
		// so far nothing to validate in OperatorGeneralUiExtras part of AccountGeneral
		
		// account number
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountDto.accountNumber", "yukon.web.modules.operator.accountGeneral.accountDto.accountNumber.accountNumberRequired");
		
		// home phone number
		if (phoneNumberFormattingService.isHasInvalidCharacters(accountDto.getHomePhone())) {
			errors.rejectValue("accountDto.homePhone", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
		}
		
		// work phone number
		if (phoneNumberFormattingService.isHasInvalidCharacters(accountDto.getWorkPhone())) {
			errors.rejectValue("accountDto.workPhone", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
		}
		
		// street/billing address
		AddressValidator addressValidator = new AddressValidator();
		
		errors.pushNestedPath("accountDto.streetAddress");
		addressValidator.validate(accountDto.getStreetAddress(), errors);
		errors.popNestedPath();
		
		errors.pushNestedPath("accountDto.billingAddress");
		addressValidator.validate(accountDto.getBillingAddress(), errors);
		errors.popNestedPath();
		
		// other
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.customerStatus", accountDto.getCustomerStatus(), 1);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.mapNumber", accountDto.getMapNumber(), 40);
		
	}

	@Autowired
	public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}
}

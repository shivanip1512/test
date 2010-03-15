package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.web.common.validation.YukonValidationUtils;
import com.cannontech.web.stars.dr.operator.OperatorAccountController.AccountGeneral;

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
		
		// street address
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.streetAddress.locationAddress1", accountDto.getStreetAddress().getLocationAddress1(), 40);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.streetAddress.locationAddress2", accountDto.getStreetAddress().getLocationAddress2(), 40);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.streetAddress.cityName", accountDto.getStreetAddress().getCityName(), 32);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.streetAddress.stateCode", accountDto.getStreetAddress().getStateCode(), 2);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.streetAddress.zipCode", accountDto.getStreetAddress().getZipCode(), 12);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.streetAddress.county", accountDto.getStreetAddress().getCounty(), 30);

		// billing address
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.billingAddress.locationAddress1", accountDto.getBillingAddress().getLocationAddress1(), 40);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.billingAddress.locationAddress2", accountDto.getBillingAddress().getLocationAddress2(), 40);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.billingAddress.cityName", accountDto.getBillingAddress().getCityName(), 32);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.billingAddress.stateCode", accountDto.getBillingAddress().getStateCode(), 2);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.billingAddress.zipCode", accountDto.getBillingAddress().getZipCode(), 12);
		
		// other
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.customerStatus", accountDto.getCustomerStatus(), 1);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.mapNumber", accountDto.getMapNumber(), 40);
		
	}

	@Autowired
	public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}
}

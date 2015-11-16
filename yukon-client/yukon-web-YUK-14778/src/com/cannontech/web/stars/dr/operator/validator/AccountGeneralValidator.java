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
import com.cannontech.web.stars.dr.operator.model.OperatorGeneralUiExtras;

public class AccountGeneralValidator extends SimpleValidator<AccountGeneral> {

	private PhoneNumberFormattingService phoneNumberFormattingService;
	
	public AccountGeneralValidator() {
		super(AccountGeneral.class);
	}

	@Override
	public void doValidation(AccountGeneral accountGeneral, Errors errors) {

		AccountDto accountDto = accountGeneral.getAccountDto();
		OperatorGeneralUiExtras operatorGeneralUiExtras = accountGeneral.getOperatorGeneralUiExtras();
		
		// account number
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountDto.accountNumber", "yukon.web.modules.operator.accountGeneral.accountDto.accountNumber.accountNumberRequired");
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.accountNumber", accountDto.getAccountNumber(), 40);
		
		// company name
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.companyName", accountDto.getCompanyName(), 80);
		
		// customer number
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.customerNumber", accountDto.getCustomerNumber(), 64);
		
		// name
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.firstName", accountDto.getFirstName(), 120);
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.lastName", accountDto.getLastName(), 120);
		
		// home phone number
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.homePhone", accountDto.getHomePhone(), 130);
		if (phoneNumberFormattingService.isHasInvalidCharacters(accountDto.getHomePhone())) {
			errors.rejectValue("accountDto.homePhone", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
		}
		
		// work phone number
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.workPhone", accountDto.getWorkPhone(), 130);
		if (phoneNumberFormattingService.isHasInvalidCharacters(accountDto.getWorkPhone())) {
			errors.rejectValue("accountDto.workPhone", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
		}
		
		// email
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.emailAddress", accountDto.getEmailAddress(), 130);
		
		// alt tracking number
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.altTrackingNumber", accountDto.getAltTrackingNumber(), 64);
		
		// account notes
		YukonValidationUtils.checkExceedsMaxLength(errors, "operatorGeneralUiExtras.notes", operatorGeneralUiExtras.getNotes(), 200);
		
		// service address
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.mapNumber", accountDto.getMapNumber(), 40);
		
		YukonValidationUtils.checkExceedsMaxLength(errors, "operatorGeneralUiExtras.accountSiteNotes", operatorGeneralUiExtras.getAccountSiteNotes(), 300);

		AddressValidator addressValidator = new AddressValidator();
		errors.pushNestedPath("accountDto.streetAddress");
		addressValidator.validate(accountDto.getStreetAddress(), errors);
		errors.popNestedPath();
		
		// billing address
		errors.pushNestedPath("accountDto.billingAddress");
		addressValidator.validate(accountDto.getBillingAddress(), errors);
		errors.popNestedPath();
		
		// site information
		SiteInformationValidator siteInformationValidator = new SiteInformationValidator();
		errors.pushNestedPath("accountDto.siteInfo");
		siteInformationValidator.validate(accountDto.getSiteInfo(), errors);
		errors.popNestedPath();
		
		// customer status
		YukonValidationUtils.checkExceedsMaxLength(errors, "accountDto.customerStatus", accountDto.getCustomerStatus(), 1);
		
	}

	@Autowired
	public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}
}

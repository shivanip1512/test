package com.cannontech.common.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.model.Address;

public class AddressValidator extends SimpleValidator<Address> {

	public AddressValidator() {
		super(Address.class);
	}
	
	@Override
	protected void doValidation(Address target, Errors errors) {
		
		YukonValidationUtils.checkExceedsMaxLength(errors, "locationAddress1", target.getLocationAddress1(), 100);
		YukonValidationUtils.checkExceedsMaxLength(errors, "locationAddress2", target.getLocationAddress2(), 100);
		YukonValidationUtils.checkExceedsMaxLength(errors, "cityName", target.getCityName(), 32);
		YukonValidationUtils.checkExceedsMaxLength(errors, "stateCode", target.getStateCode(), 2);
		YukonValidationUtils.checkExceedsMaxLength(errors, "zipCode", target.getZipCode(), 12);
		YukonValidationUtils.checkExceedsMaxLength(errors, "county", target.getCounty(), 30);
	}
}

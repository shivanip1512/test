package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.dr.account.model.CustomerResidence;

public class CustomerResidenceValidator extends SimpleValidator<CustomerResidence> {

	public CustomerResidenceValidator() {
		super(CustomerResidence.class);
	}
	
	@Override
	public void doValidation(CustomerResidence customerResidence, Errors errors) {

		YukonValidationUtils.checkExceedsMaxLength(errors, "notes", customerResidence.getNotes(), 300);
	}
}

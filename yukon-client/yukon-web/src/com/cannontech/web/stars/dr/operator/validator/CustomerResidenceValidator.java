package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.cannontech.stars.dr.account.model.CustomerResidence;
import com.cannontech.web.common.validation.YukonValidationUtils;

public class CustomerResidenceValidator implements Validator {

	@Override
	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return CustomerResidence.class.isAssignableFrom(clazz); 
	}
	
	@Override
	public void validate(Object target, Errors errors) {

		CustomerResidence customerResidence = (CustomerResidence)target;
		
		YukonValidationUtils.checkExceedsMaxLength(errors, "notes", customerResidence.getNotes(), 300);
	}
}

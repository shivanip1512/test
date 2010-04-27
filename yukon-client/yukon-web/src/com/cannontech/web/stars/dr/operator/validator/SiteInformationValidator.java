package com.cannontech.web.stars.dr.operator.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.model.SiteInformation;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class SiteInformationValidator extends SimpleValidator<SiteInformation> {
	
	public SiteInformationValidator() {
		super(SiteInformation.class);
	}
	
	@Override
	public void doValidation(SiteInformation siteInformation, Errors errors) {
		
		YukonValidationUtils.checkExceedsMaxLength(errors, "feeder", siteInformation.getFeeder(), 20);
		YukonValidationUtils.checkExceedsMaxLength(errors, "pole", siteInformation.getPole(), 20);
		YukonValidationUtils.checkExceedsMaxLength(errors, "transformerSize", siteInformation.getTransformerSize(), 20);
		YukonValidationUtils.checkExceedsMaxLength(errors, "serviceVoltage", siteInformation.getServiceVoltage(), 20);
	}
}

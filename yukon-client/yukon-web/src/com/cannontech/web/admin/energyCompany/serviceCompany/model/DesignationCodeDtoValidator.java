package com.cannontech.web.admin.energyCompany.serviceCompany.model;

import org.springframework.validation.Errors;

import com.cannontech.common.model.DesignationCodeDto;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class DesignationCodeDtoValidator extends SimpleValidator<DesignationCodeDto> {

    public DesignationCodeDtoValidator() {
        super(DesignationCodeDto.class);
    }

    @Override
    protected void doValidation(DesignationCodeDto target, Errors errors) {
        YukonValidationUtils.checkExceedsMaxLength(errors, "designationCode", target.getValue(), 60);
    }

}

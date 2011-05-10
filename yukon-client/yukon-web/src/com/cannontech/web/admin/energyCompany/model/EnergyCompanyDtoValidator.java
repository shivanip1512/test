package com.cannontech.web.admin.energyCompany.model;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.model.EnergyCompanyDto;
import com.cannontech.util.Validator;

public class EnergyCompanyDtoValidator extends SimpleValidator<EnergyCompanyDto> {
    
    public EnergyCompanyDtoValidator() {
        super(EnergyCompanyDto.class);
    }

    @Override
    public void doValidation(EnergyCompanyDto energyCompanyDto, Errors errors) {
        /* Company Name */
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.modules.adminSetup.createEnergyCompany.name.required");
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", energyCompanyDto.getName(), 60);
        
        /* Email */
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "yukon.web.modules.adminSetup.createEnergyCompany.email.required");
        if (StringUtils.isNotBlank(energyCompanyDto.getEmail()) && !Validator.isEmailAddress(energyCompanyDto.getEmail())) {
            errors.rejectValue("email", "yukon.web.modules.adminSetup.createEnergyCompany.email.invalid");
        }
        
        /* Primary Operator Group */
        if (energyCompanyDto.getPrimaryOperatorGroupId() == null) {
            errors.rejectValue("primaryOperatorGroupId", "yukon.web.modules.adminSetup.createEnergyCompany.primaryOperatorGroup.required");
        }
        
        /* Default Operator Login */
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminUsername", "yukon.web.modules.adminSetup.createEnergyCompany.adminUsername.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminPassword1", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword1.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminPassword2", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword2.required");
        if (StringUtils.isNotBlank(energyCompanyDto.getAdminPassword1()) && StringUtils.isNotBlank(energyCompanyDto.getAdminPassword2())
                && !energyCompanyDto.getAdminPassword1().equals(energyCompanyDto.getAdminPassword2())) {
            errors.rejectValue("adminPassword1", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword.mismatch");
            errors.rejectValue("adminPassword2", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword.mismatch");
        }
        
        /* Secondary Operator Login, if any fields are not blank, all fields must be filled in. */
        if (StringUtils.isNotBlank(energyCompanyDto.getAdmin2Username())
                || StringUtils.isNotBlank(energyCompanyDto.getAdmin2Password1())
                || StringUtils.isNotBlank(energyCompanyDto.getAdmin2Password2())) {
            /* Verify not blank */
            if (StringUtils.isBlank(energyCompanyDto.getAdmin2Username())) {
                errors.rejectValue("admin2Username", "yukon.web.modules.adminSetup.createEnergyCompany.adminUsername.required");
            }
            if (StringUtils.isBlank(energyCompanyDto.getAdmin2Password1())) {
                errors.rejectValue("admin2Password1", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword1.required");
            }
            if (StringUtils.isBlank(energyCompanyDto.getAdmin2Password2())) {
                errors.rejectValue("admin2Password2", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword2.required");
            }
            /* Verify matching passwords */
            if (StringUtils.isNotBlank(energyCompanyDto.getAdmin2Password1()) && StringUtils.isNotBlank(energyCompanyDto.getAdmin2Password2())
                    && !energyCompanyDto.getAdmin2Password1().equals(energyCompanyDto.getAdmin2Password2())) {
                errors.rejectValue("admin2Password1", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword.mismatch");
                errors.rejectValue("admin2Password2", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword.mismatch");
            }
        }
        
    }
    
}
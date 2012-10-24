package com.cannontech.web.admin.energyCompany.model;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.model.EnergyCompanyDto;
import com.cannontech.util.Validator;

public class EnergyCompanyDtoValidator extends SimpleValidator<EnergyCompanyDto> {
    
    @Autowired YukonUserDao userDao;
    
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
        YukonValidationUtils.checkExceedsMaxLength(errors, "email", energyCompanyDto.getEmail(), 130);

        /* Primary Operator Group */
        if (energyCompanyDto.getPrimaryOperatorUserGroupId() == null) {
            errors.rejectValue("primaryOperatorUserGroupId", "yukon.web.modules.adminSetup.createEnergyCompany.primaryOperatorGroup.required");
        }
        
        /* Default Operator Login */
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminUsername", "yukon.web.modules.adminSetup.createEnergyCompany.adminUsername.required");
        YukonValidationUtils.checkExceedsMaxLength(errors, "adminUsername", energyCompanyDto.getAdminUsername(), 64);
        LiteYukonUser usernameCheckUser = userDao.findUserByUsername(energyCompanyDto.getAdminUsername());
        if (usernameCheckUser != null) {
            errors.rejectValue("adminUsername", "yukon.web.modules.adminSetup.createEnergyCompany.adminUsername.unavailable");
        }
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminPassword1", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword1.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminPassword2", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword2.required");
        if (StringUtils.isNotBlank(energyCompanyDto.getAdminPassword1()) && StringUtils.isNotBlank(energyCompanyDto.getAdminPassword2())
                && !energyCompanyDto.getAdminPassword1().equals(energyCompanyDto.getAdminPassword2())) {
            errors.rejectValue("adminPassword1", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword.mismatch");
            errors.rejectValue("adminPassword2", "yukon.web.modules.adminSetup.createEnergyCompany.adminPassword.mismatch");
        }
    }
}

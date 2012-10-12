package com.cannontech.web.admin.energyCompany.general.model;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.AddressValidator;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.util.Validator;
import com.cannontech.web.stars.dr.operator.validator.ContactNotificationDtoValidator;

public class GeneralInfoValidator extends SimpleValidator<GeneralInfo> {
    
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private PhoneNumberFormattingService phoneNumberFormattingService;
    boolean ecNameChange = true;

    public GeneralInfoValidator() {
        super(GeneralInfo.class);
    }

    @Override
    public void doValidation(GeneralInfo generalInfo, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.modules.adminSetup.generalInfo.invalidEnergyCompanyName");
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", generalInfo.getName(), 60);
        
        /* Validate Address */
        AddressValidator addressValidator = new AddressValidator();
        errors.pushNestedPath("address");
        addressValidator.validate(generalInfo.getAddress(), errors);
        errors.popNestedPath();
        
        /* Main Phone Number */
        String phone = generalInfo.getPhone();
        if (StringUtils.isNotBlank(phone)  && phoneNumberFormattingService.isHasInvalidCharacters(phone)) {
            errors.rejectValue("phone", "yukon.web.modules.adminSetup.generalInfo.invalidPhoneNumber");
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "phone", phone, ContactNotificationDtoValidator.MAX_NOTIFICATION_LENGTH);
        
        /* Fax Number */
        String fax = generalInfo.getFax();
        if (StringUtils.isNotBlank(fax) && phoneNumberFormattingService.isHasInvalidCharacters(fax)) {
            errors.rejectValue("fax", "yukon.web.modules.adminSetup.generalInfo.invalidFaxNumber");
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "fax", fax, ContactNotificationDtoValidator.MAX_NOTIFICATION_LENGTH);
        
        /* Email */
        String email = generalInfo.getEmail();
        if (StringUtils.isNotBlank(email) && !Validator.isEmailAddress(email)) {
            errors.rejectValue("email", "yukon.web.modules.adminSetup.generalInfo.invalidEmail");
        }

        if (ecNameChange && energyCompanyDao.findEnergyCompanyByName(generalInfo.getName()) != null) {
            // Found an energycompany already using this name.
            errors.rejectValue("name", "yukon.web.modules.adminSetup.createEnergyCompany.name.unavailable");
        }

    }

    public void setEcNameChange(boolean ecNameChange) {
        this.ecNameChange = ecNameChange;
    }

}
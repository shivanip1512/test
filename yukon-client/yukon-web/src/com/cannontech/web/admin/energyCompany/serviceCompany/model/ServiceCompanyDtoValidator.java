package com.cannontech.web.admin.energyCompany.serviceCompany.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.cannontech.common.model.Address;
import com.cannontech.common.model.DesignationCodeDto;
import com.cannontech.common.model.ServiceCompanyDto;
import com.cannontech.common.validator.AddressValidator;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.util.Validator;

public class ServiceCompanyDtoValidator extends SimpleValidator<ServiceCompanyDto> {

    private PhoneNumberFormattingService phoneNumberFormattingService;
    
    public ServiceCompanyDtoValidator() {
        super(ServiceCompanyDto.class);
    }

    @Override
    protected void doValidation(ServiceCompanyDto serviceCompany, Errors errors) {
        //Check Address
        AddressValidator addressValidator = new AddressValidator();
        errors.pushNestedPath("address");
            addressValidator.validate(new Address(serviceCompany.getAddress()), errors);
        errors.popNestedPath();
        
        //Check ServiceCompany
        //Name
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "companyName", "yukon.web.modules.adminSetup.serviceCompany.serviceCompanyDto.serviceCompany.nameRequired");
        YukonValidationUtils.checkExceedsMaxLength(errors, "companyName", serviceCompany.getCompanyName(), 40);
        
        //Main Phone
        String mainPhone = serviceCompany.getMainPhoneNumber();
        if (mainPhone != null && phoneNumberFormattingService.isHasInvalidCharacters(mainPhone)) {
            errors.rejectValue("mainPhoneNumber", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "mainPhoneNumber", serviceCompany.getMainPhoneNumber(), 14);
        
        //Main Fax
        String mainFax = serviceCompany.getMainFaxNumber();
        if (mainFax != null && phoneNumberFormattingService.isHasInvalidCharacters(mainFax)) {
            errors.rejectValue("mainFaxNumber", "yukon.web.modules.operator.accountGeneral.invalidPhoneNumber");
        }
        YukonValidationUtils.checkExceedsMaxLength(errors, "mainFaxNumber", serviceCompany.getMainFaxNumber(), 14);
        
        //HIType?
        
        //Check Primary Contact
        errors.pushNestedPath("primaryContact");
            YukonValidationUtils.checkExceedsMaxLength(errors, "contFirstName", serviceCompany.getCompanyName(), 120);
            YukonValidationUtils.checkExceedsMaxLength(errors, "contLastName", serviceCompany.getCompanyName(), 120);
        errors.popNestedPath();
        
        //Check email
        YukonValidationUtils.checkExceedsMaxLength(errors, "emailContactNotification", serviceCompany.getEmailContactNotification(), 130);
        if(serviceCompany.getEmailContactNotification().length() > 0 && !Validator.isEmailAddress(serviceCompany.getEmailContactNotification())) {
            errors.rejectValue("emailContactNotification", "yukon.web.modules.operator.accountImport.error.invalidEmail");
        }
        
        //Check Designation Codes
        DesignationCodeDtoValidator designationCodeValidator = new DesignationCodeDtoValidator();
        errors.pushNestedPath("designationCodes");
            for(DesignationCodeDto desginationCode : serviceCompany.getDesignationCodes()) {
                designationCodeValidator.validate(desginationCode, errors);
            }
        errors.popNestedPath();
    }
    
    @Autowired
    public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
        this.phoneNumberFormattingService = phoneNumberFormattingService;
    }
}

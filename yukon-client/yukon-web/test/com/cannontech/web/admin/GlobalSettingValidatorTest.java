package com.cannontech.web.admin;

import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.cannontech.system.GlobalSettingSubCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.admin.YukonConfigurationController.GlobalSettingsEditorBean;

public class GlobalSettingValidatorTest {

    private GlobalSettingValidator service;
    private GlobalSettingsEditorBean command;
    private Errors errors;

    @Before
    public void setup() {
        service = new GlobalSettingValidator();
    }

    @Test
    public void testValidator() {
        Map<GlobalSettingType, Object> globalSettings = new HashMap<>();

        // Validation for YUKON_SERVICES category
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.YUKON_SERVICES);
        globalSettings.put(GlobalSettingType.NETWORK_MANAGER_ADDRESS, "http://127.0.0.1");
        globalSettings.put(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER, "http://127.0.0.1");
        globalSettings.put(GlobalSettingType.JMS_BROKER_HOST, "127.0.0.1");
        globalSettings.put(GlobalSettingType.SMTP_HOST, "127.0.0.1");
        globalSettings.put(GlobalSettingType.MAIL_FROM_ADDRESS, "test@eaton.com");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());

        // Validation for DR category
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DR);
        globalSettings.put(GlobalSettingType.ECOBEE_SERVER_URL, "http://127.0.0.1");
        globalSettings.put(GlobalSettingType.HONEYWELL_SERVER_URL, "http://127.0.0.1");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");

        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());

        // Validation for AUTHENTICATION category
        command.setCategory(GlobalSettingSubCategory.AUTHENTICATION);
        globalSettings.put(GlobalSettingType.SERVER_ADDRESS, "");
        globalSettings.put(GlobalSettingType.LDAP_SERVER_ADDRESS, "127.0.0.1");
        globalSettings.put(GlobalSettingType.AD_SERVER_ADDRESS, "127.0.0.1");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());

        // Validation for MISC
        command.setCategory(GlobalSettingSubCategory.MISC);
        globalSettings.put(GlobalSettingType.CONTACT_EMAIL, "test@eaton.com");
        globalSettings.put(GlobalSettingType.SYSTEM_TIMEZONE, "America/Chicago");
        globalSettings.put(GlobalSettingType.HTTP_PROXY, "12.12.141.22:8080");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
    }
}

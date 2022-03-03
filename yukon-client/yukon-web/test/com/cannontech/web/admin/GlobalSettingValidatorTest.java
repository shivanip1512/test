package com.cannontech.web.admin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.cannontech.i18n.YukonUserContextMessageSourceResolverMock;
import com.cannontech.system.GlobalSettingSubCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.GlobalSettingTypeValidators;
import com.cannontech.web.admin.YukonConfigurationController.GlobalSettingsEditorBean;

public class GlobalSettingValidatorTest {

    private GlobalSettingValidator service;
    private GlobalSettingsEditorBean command;
    private Errors errors;

    @BeforeEach
    public void setup() {
        service = new GlobalSettingValidator();
        StaticMessageSource messageSource = new StaticMessageSource();
        {
            messageSource.addMessage("yukon.common.setting.JMS_BROKER_PORT", Locale.US, "Message Broker Port");
            messageSource.addMessage("yukon.common.setting.SMTP_PORT", Locale.US, "Smtp Port");
            messageSource.addMessage("yukon.common.setting.LAST_COMMUNICATION_HOURS", Locale.US, "Last Communication Hours");
            messageSource.addMessage("yukon.common.setting.LAST_RUNTIME_HOURS", Locale.US, "Last Runtime Hours");
            messageSource.addMessage("yukon.common.setting.AUTH_PORT", Locale.US, "Auth Port");
            messageSource.addMessage("yukon.common.setting.ACCT_PORT", Locale.US, "Acct Port");
            messageSource.addMessage("yukon.common.setting.LDAP_SERVER_PORT", Locale.US, "LDAP Server Port");
            messageSource.addMessage("yukon.common.setting.AD_SERVER_PORT", Locale.US, "AD Server Port");
            messageSource.addMessage("yukon.common.setting.PRODUCER_WINDOW_SIZE", Locale.US, "Producer Window Size");
            messageSource.addMessage("yukon.common.setting.CLOUD_DATA_SENDING_FREQUENCY", Locale.US, "Cloud Data Sending Frequency");
            messageSource.addMessage("yukon.common.setting.FDR_DNPSLAVE_APPLICATION_FRAGMENT_SIZE", Locale.US, "FDR DNP Slave Application Fragment Size ");
            messageSource.addMessage("yukon.common.setting.RFN_INCOMING_DATA_TIMESTAMP_LIMIT", Locale.US, "RFN Incoming Data Timestamp Limit");
            messageSource.addMessage("yukon.common.setting.DATA_AVAILABILITY_WINDOW_IN_DAYS", Locale.US, "Data Collection: Data Availability Window");
            messageSource.addMessage("yukon.common.setting.ITRON_HCM_DATA_COLLECTION_MINUTES", Locale.US, "Itron HCM Data Collection Interval");
            messageSource.addMessage("yukon.common.setting.ITRON_HCM_RESPONSE_TIMEOUT_SECONDS", Locale.US, "Itron HCM Response Timeout");
            messageSource.addMessage("yukon.common.setting.RUNTIME_CALCULATION_INTERVAL_HOURS", Locale.US, "Runtime Calculation Interval");
            messageSource.addMessage("yukon.common.setting.EATON_CLOUD_DEVICE_CREATION_INTERVAL", Locale.US, "Eaton Cloud Cellular LCR auto creation interval");
        }
        YukonUserContextMessageSourceResolverMock messageResolver = new YukonUserContextMessageSourceResolverMock();
        messageResolver.setMessageSource(messageSource);
        ReflectionTestUtils.setField(GlobalSettingTypeValidators.class, "messageResolver", messageResolver);
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
        globalSettings.put(GlobalSettingType.JMS_BROKER_PORT, 61616);
        globalSettings.put(GlobalSettingType.SMTP_HOST, "127.0.0.1");
        globalSettings.put(GlobalSettingType.SMTP_PORT, null);
        globalSettings.put(GlobalSettingType.MAIL_FROM_ADDRESS, "test@eaton.com");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.YUKON_SERVICES);
        globalSettings.put(GlobalSettingType.NETWORK_MANAGER_ADDRESS, "http://localhost");
        globalSettings.put(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER, "http://RFNUpdateServer");
        globalSettings.put(GlobalSettingType.JMS_BROKER_HOST, "BROKERHOST");
        globalSettings.put(GlobalSettingType.SMTP_PORT, 587);
        globalSettings.put(GlobalSettingType.SMTP_HOST, "SMTPHOST");
        
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.YUKON_SERVICES);
        globalSettings.put(GlobalSettingType.NETWORK_MANAGER_ADDRESS, "htt://127.0.0.1");
        globalSettings.put(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER, "http://127.0.0");
        globalSettings.put(GlobalSettingType.JMS_BROKER_HOST, "127.0.0/1");
        globalSettings.put(GlobalSettingType.SMTP_HOST, ":127.0.0.1");
        globalSettings.put(GlobalSettingType.MAIL_FROM_ADDRESS, "testeaton.com");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.getErrorCount() == 5,
                "Incorrect global setting values for category "+GlobalSettingSubCategory.YUKON_SERVICES);
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.YUKON_SERVICES);
        globalSettings.put(GlobalSettingType.NETWORK_MANAGER_ADDRESS, "http://NM?ADDRESS");
        globalSettings.put(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER, "http://RFN*FIRMWARE");
        globalSettings.put(GlobalSettingType.JMS_BROKER_HOST, "BROKERHOST:8080");
        globalSettings.put(GlobalSettingType.SMTP_HOST, "SMTP))SMTP");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.getErrorCount() == 4,
                "Incorrect global setting values for category "+GlobalSettingSubCategory.YUKON_SERVICES);

        // Validation for DR category
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DR);
        globalSettings.put(GlobalSettingType.ECOBEE_SERVER_URL, "http://127.0.0.1");
        globalSettings.put(GlobalSettingType.HONEYWELL_SERVER_URL, "http://127.0.0.1");
//        globalSettings.put(GlobalSettingType.NEST_SERVER_URL, "http://127.0.0.1");
        globalSettings.put(GlobalSettingType.ITRON_HCM_API_URL, "http://127.0.0.1");
        globalSettings.put(GlobalSettingType.LAST_COMMUNICATION_HOURS, 60);
        globalSettings.put(GlobalSettingType.LAST_RUNTIME_HOURS, 60);
        globalSettings.put(GlobalSettingType.ITRON_HCM_DATA_COLLECTION_MINUTES, 15);
        globalSettings.put(GlobalSettingType.ITRON_HCM_RESPONSE_TIMEOUT_SECONDS, 120);
        globalSettings.put(GlobalSettingType.RUNTIME_CALCULATION_INTERVAL_HOURS, 2);
        globalSettings.put(GlobalSettingType.EATON_CLOUD_DEVICE_CREATION_INTERVAL, 24);
        globalSettings.put(GlobalSettingType.EATON_CLOUD_DEVICE_READ_INTERVAL_MINUTES, 1);
        globalSettings.put(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID, "fcbd4387-886b-4f89-98d0-7801ff7b75e3");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");

        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());

        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DR);
        globalSettings.put(GlobalSettingType.ECOBEE_SERVER_URL, "http://ECOBEESERVER");
        globalSettings.put(GlobalSettingType.HONEYWELL_SERVER_URL, "http://HONEYWELLSERVER");
//        globalSettings.put(GlobalSettingType.NEST_SERVER_URL, "http://NESTSERVER");
        globalSettings.put(GlobalSettingType.ITRON_HCM_API_URL, "http://ITRONSERVER");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");

        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());

        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DR);
        globalSettings.put(GlobalSettingType.ECOBEE_SERVER_URL, "http//127.0.0.1");
        globalSettings.put(GlobalSettingType.HONEYWELL_SERVER_URL, "htt://127.0.0.1");
//        globalSettings.put(GlobalSettingType.NEST_SERVER_URL, "htt://127.0.0.1");
        globalSettings.put(GlobalSettingType.ITRON_HCM_API_URL, "htt://127.0.0.1");
        globalSettings.put(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID, "fcbd4387-886b-4f89-98d0-7801ff7b75e30");
        
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");

        service.doValidation(command, errors);
        assertTrue(errors.getErrorCount() == 4,
                "Incorrect global setting values for category "+GlobalSettingSubCategory.DR);
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DR);
        globalSettings.put(GlobalSettingType.ECOBEE_SERVER_URL, "http://ECOBEE>SERVER");
        globalSettings.put(GlobalSettingType.HONEYWELL_SERVER_URL, "http://HONEYWELL(SERVER");
//        globalSettings.put(GlobalSettingType.NEST_SERVER_URL, "http://NEST(SERVER");
        globalSettings.put(GlobalSettingType.ITRON_HCM_API_URL, "http://ITRON(SERVER");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");

        service.doValidation(command, errors);
        assertTrue(errors.getErrorCount() == 4,
                "Incorrect global setting values for category "+GlobalSettingSubCategory.DR);

        // InValid Runtime Calculation Value
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DR);
        globalSettings.put(GlobalSettingType.RUNTIME_CALCULATION_INTERVAL_HOURS, 0);
        command.setValues(globalSettings);
        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.hasErrors());

        // InValid Runtime Calculation Value
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DR);
        globalSettings.put(GlobalSettingType.RUNTIME_CALCULATION_INTERVAL_HOURS, 25);
        command.setValues(globalSettings);
        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.hasErrors());

        // Validation for AUTHENTICATION category
        command.setCategory(GlobalSettingSubCategory.AUTHENTICATION);
        globalSettings.put(GlobalSettingType.SERVER_ADDRESS, "");
        globalSettings.put(GlobalSettingType.AUTH_PORT, 1812);
        globalSettings.put(GlobalSettingType.ACCT_PORT, 1813);
        globalSettings.put(GlobalSettingType.LDAP_SERVER_ADDRESS, "127.0.0.1");
        globalSettings.put(GlobalSettingType.LDAP_SERVER_PORT, 389);
        globalSettings.put(GlobalSettingType.AD_SERVER_ADDRESS, "127.0.0.1");
        globalSettings.put(GlobalSettingType.AD_SERVER_PORT, "1234");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
        
     // Validation for AUTHENTICATION category
        command.setCategory(GlobalSettingSubCategory.AUTHENTICATION);
        globalSettings.put(GlobalSettingType.SERVER_ADDRESS, "");
        globalSettings.put(GlobalSettingType.LDAP_SERVER_ADDRESS, "LDAPServer");
        globalSettings.put(GlobalSettingType.AD_SERVER_ADDRESS, "ad.server.address");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
        
        //Validation for AD_SERVER_ADDRESS and AD_SERVER_PORT with single space
        command.setCategory(GlobalSettingSubCategory.AUTHENTICATION);
        globalSettings.put(GlobalSettingType.AD_SERVER_ADDRESS, "1.1.1.1 5.5.5.5");
        globalSettings.put(GlobalSettingType.AD_SERVER_PORT, "1234 3345");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
        
       //Validation for AD_SERVER_ADDRESS and AD_SERVER_PORT with multiple spaces
        command.setCategory(GlobalSettingSubCategory.AUTHENTICATION);
        globalSettings.put(GlobalSettingType.AD_SERVER_ADDRESS, "1.1.1.1        5.5.5.5");
        globalSettings.put(GlobalSettingType.AD_SERVER_PORT, "1234        3345");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());

        command.setCategory(GlobalSettingSubCategory.AUTHENTICATION);
        globalSettings.put(GlobalSettingType.SERVER_ADDRESS, "/127.0.0.1");
        globalSettings.put(GlobalSettingType.LDAP_SERVER_ADDRESS, "127.0.0.1?");
        globalSettings.put(GlobalSettingType.AD_SERVER_ADDRESS, "12*.0.0.1");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.getErrorCount() == 3,
                "Incorrect global setting values for category "+GlobalSettingSubCategory.AUTHENTICATION);

        command.setCategory(GlobalSettingSubCategory.AUTHENTICATION);
        globalSettings.put(GlobalSettingType.SERVER_ADDRESS, "SERVER?ADDRESS");
        globalSettings.put(GlobalSettingType.LDAP_SERVER_ADDRESS, "LDAP:SERVER");
        globalSettings.put(GlobalSettingType.AD_SERVER_ADDRESS, "AD*SERVER");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.getErrorCount() == 3,
                "Incorrect global setting values for category "+GlobalSettingSubCategory.AUTHENTICATION);

        
        // Validation for MISC
        command.setCategory(GlobalSettingSubCategory.MISC);
        globalSettings.put(GlobalSettingType.CONTACT_EMAIL, "test@eaton.com");
        globalSettings.put(GlobalSettingType.SYSTEM_TIMEZONE, "America/Chicago");
        globalSettings.put(GlobalSettingType.HTTP_PROXY, "12.12.141.22:8080");
        globalSettings.put(GlobalSettingType.PRODUCER_WINDOW_SIZE, 512);
        globalSettings.put(GlobalSettingType.CLOUD_DATA_SENDING_FREQUENCY, 15);
        globalSettings.put(GlobalSettingType.FDR_DNPSLAVE_APPLICATION_FRAGMENT_SIZE, 249);
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
        
        command.setCategory(GlobalSettingSubCategory.MISC);
        globalSettings.put(GlobalSettingType.CONTACT_EMAIL, "test@eatoncom");
        globalSettings.put(GlobalSettingType.SYSTEM_TIMEZONE, "AmericaChicago");
        globalSettings.put(GlobalSettingType.HTTP_PROXY, "12.12.141.228080");
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.getErrorCount() == 3,
                "Incorrect global setting values for category "+GlobalSettingSubCategory.MISC);
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.AMI);
        globalSettings.put(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT, 3);
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");

        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.AMI);
        globalSettings.put(GlobalSettingType.RFN_INCOMING_DATA_TIMESTAMP_LIMIT, 280);
        command.setValues(globalSettings);

        errors = new BeanPropertyBindingResult(command, "ValidationResult");

        service.doValidation(command, errors);
        assertTrue(errors.hasErrors());
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.WEB_SERVER);
        globalSettings.put(GlobalSettingType.YUKON_EXTERNAL_URL, "http://127.0.0.1:8080");
        globalSettings.put(GlobalSettingType.YUKON_INTERNAL_URL, "http://127.0.0.1");
        command.setValues(globalSettings);
        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());

        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.WEB_SERVER);
        globalSettings.put(GlobalSettingType.YUKON_EXTERNAL_URL, null);
        globalSettings.put(GlobalSettingType.YUKON_INTERNAL_URL, null);
        command.setValues(globalSettings);
        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.WEB_SERVER);
        globalSettings.put(GlobalSettingType.YUKON_INTERNAL_URL, "http127.0.1");
        command.setValues(globalSettings);
        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.hasErrors());
        
        //Test Data Availability Window validation
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DASHBOARD_WIDGET);
        globalSettings.put(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS, 5);
        command.setValues(globalSettings);
        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DASHBOARD_WIDGET);
        globalSettings.put(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS, 0);
        command.setValues(globalSettings);
        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.hasErrors());
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DASHBOARD_WIDGET);
        globalSettings.put(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS, 8);
        command.setValues(globalSettings);
        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertTrue(errors.hasErrors());
        
        command = new GlobalSettingsEditorBean();
        command.setCategory(GlobalSettingSubCategory.DASHBOARD_WIDGET);
        globalSettings.put(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS, 7);
        command.setValues(globalSettings);
        errors = new BeanPropertyBindingResult(command, "ValidationResult");
        service.doValidation(command, errors);
        assertFalse(errors.hasErrors());

    }
}

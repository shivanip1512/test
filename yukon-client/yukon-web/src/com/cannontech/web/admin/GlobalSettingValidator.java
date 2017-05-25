package com.cannontech.web.admin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.joda.time.DateTimeZone;
import org.springframework.validation.Errors;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.admin.YukonConfigurationController.GlobalSettingsEditorBean;

public class GlobalSettingValidator extends SimpleValidator<GlobalSettingsEditorBean> {
    private static Map<GlobalSettingType, TypeValidator> validators =  new HashMap<>();
    private static String baseKey = "yukon.web.modules.adminSetup.config.error.";

    // To add validation for a field, add the TypeValidator to validators map
    static {
        validators.put(GlobalSettingType.HTTP_PROXY, new TypeValidator() {
            Pattern urlWithPortMatcher = Pattern.compile("\\s*(.*?):(\\d+)\\s*");
            @Override
            public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
                String httpProxy = (String) value;
                YukonValidationUtils.checkIsBlankOrExceedsMaxLength(errors, "values[HTTP_PROXY]", httpProxy, false, 1000);

                if (!StringUtils.isBlank(httpProxy)
                        && !httpProxy.equals("none")
                        && !urlWithPortMatcher.matcher(httpProxy).matches()) {
                    errors.rejectValue("values["+globalSettingType+"]", baseKey + "invalidProxy", null,"");
                }
            }
        });
        validators.put(GlobalSettingType.SYSTEM_TIMEZONE, new TypeValidator() {
            @Override
            public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
                String timeZoneId = (String) value;
                YukonValidationUtils.checkExceedsMaxLength(errors, "values[SYSTEM_TIMEZONE]", timeZoneId, 1000);
                if (StringUtils.isNotBlank(timeZoneId)) {
                    try {
                        TimeZone timeZone=CtiUtilities.getValidTimeZone(timeZoneId);
                        DateTimeZone.forTimeZone(timeZone);
                    } catch(BadConfigurationException e) {
                        errors.rejectValue("values["+globalSettingType+"]", baseKey + "invalidTimeZone", null,"");
                    } catch (IllegalArgumentException e){
                        errors.rejectValue("values["+globalSettingType+"]", baseKey + "invalidJodaTimeZone",null,"");
                    }
                }
            }
        });

        validators.put(GlobalSettingType.ECOBEE_SERVER_URL, getURLValidator());
        validators.put(GlobalSettingType.HONEYWELL_SERVER_URL, getURLValidator());
        validators.put(GlobalSettingType.NETWORK_MANAGER_ADDRESS, getURLValidator());
        validators.put(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER, getURLValidator());

        validators.put(GlobalSettingType.JMS_BROKER_HOST, getIPHostNameValidator());
        validators.put(GlobalSettingType.SERVER_ADDRESS, getIPHostNameValidator());
        validators.put(GlobalSettingType.LDAP_SERVER_ADDRESS, getIPHostNameValidator());
        validators.put(GlobalSettingType.AD_SERVER_ADDRESS, getIPHostNameValidator());

        validators.put(GlobalSettingType.CONTACT_EMAIL, getEmailValidator());
        validators.put(GlobalSettingType.MAIL_FROM_ADDRESS, getEmailValidator());
    }

    public GlobalSettingValidator() {
        super(GlobalSettingsEditorBean.class);
    }

    @Override
    protected void doValidation(GlobalSettingsEditorBean globalSettingsBean, Errors errors) {

        Set<GlobalSettingType> globalSettingTypes
            = GlobalSettingType.getSettingsForCategory(globalSettingsBean.getCategory());
        Map<GlobalSettingType, Object> values = globalSettingsBean.getValues();

        for (GlobalSettingType globalSettingType : globalSettingTypes) {
            if (validators.containsKey(globalSettingType)) {
                validators.get(globalSettingType).validate(values.get(globalSettingType), errors, globalSettingType);
            }
        }
    }
    
    
    private static TypeValidator getURLValidator() {
        return new TypeValidator() {
            private final String[] schemes = { "http", "https" };

            @Override
            public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
                String url = (String) value;

                UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
                if (!urlValidator.isValid(url)) {
                    errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidURL");
                }
            }
        };
    }

    private static TypeValidator getEmailValidator() {
        return new TypeValidator() {
            @Override
            public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
                boolean emailValid = EmailValidator.getInstance().isValid(value.toString());
                if (!emailValid) {
                    errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidEmail");
                }

            }
        };
    }

    private static TypeValidator getIPHostNameValidator() {
        return new TypeValidator() {
            @Override
            public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
                boolean ipValid = InetAddressValidator.getInstance().isValid(value.toString());
                if (!ipValid) {
                    try {
                        InetAddress.getByName(value.toString());
                    } catch (UnknownHostException e) {
                        errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidIPHostName");
                    }
                }
            }
        };
    }

    interface TypeValidator {
        public void validate(Object value, Errors errors, GlobalSettingType globalSetting);
    }
}

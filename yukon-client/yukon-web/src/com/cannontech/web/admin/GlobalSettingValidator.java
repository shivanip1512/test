package com.cannontech.web.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
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

    private static TypeValidator urlValidator = new TypeValidator() {
        private final String[] schemes = { "http", "https" };

        @Override
        public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
            String url = (String) value;
            if (StringUtils.isNotBlank(url)) {
                UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
                if (!urlValidator.isValid(url)) {
                    errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidURL");
                }
            }
        }
    };

    private static TypeValidator emailValidator = new TypeValidator() {
        @Override
        public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
            String email = (String) value;
            if (StringUtils.isNotBlank(email)) {
                boolean emailValid = EmailValidator.getInstance().isValid(email);
                if (!emailValid) {
                    errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidEmail");
                }
            }
        }
    };

    private static TypeValidator ipHostNameValidator = new TypeValidator() {
        Pattern ipHostNameMatcher =
            Pattern.compile("^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$");

        @Override
        public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
            String ipHost = (String) value;
            if (StringUtils.isNotBlank(ipHost)) {
                if (!ipHostNameMatcher.matcher(ipHost).matches()) {
                    errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidIPHostName");
                }
            }
        }
    };    

    // To add validation for a field, add the TypeValidator to validators map
    static {
        validators.put(GlobalSettingType.HTTP_PROXY, new TypeValidator() {
            Pattern urlWithPortMatcher = Pattern.compile("\\s*(.*?):(\\d+)\\s*");
            @Override
            public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
                String httpProxy = (String) value;
                YukonValidationUtils.checkIsBlankOrExceedsMaxLength(errors, "values[" + globalSettingType + "]", httpProxy, false, 1000);

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
                YukonValidationUtils.checkExceedsMaxLength(errors, "values["+globalSettingType+"]", timeZoneId, 1000);
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

        validators.put(GlobalSettingType.ECOBEE_SERVER_URL, urlValidator);
        validators.put(GlobalSettingType.HONEYWELL_SERVER_URL, urlValidator);
        validators.put(GlobalSettingType.NETWORK_MANAGER_ADDRESS, urlValidator);
        validators.put(GlobalSettingType.RFN_FIRMWARE_UPDATE_SERVER, urlValidator);

        validators.put(GlobalSettingType.JMS_BROKER_HOST, ipHostNameValidator);
        validators.put(GlobalSettingType.SERVER_ADDRESS, ipHostNameValidator);
        validators.put(GlobalSettingType.LDAP_SERVER_ADDRESS, ipHostNameValidator);
        validators.put(GlobalSettingType.AD_SERVER_ADDRESS, ipHostNameValidator);
        validators.put(GlobalSettingType.SMTP_HOST, ipHostNameValidator);

        validators.put(GlobalSettingType.CONTACT_EMAIL, emailValidator);
        validators.put(GlobalSettingType.MAIL_FROM_ADDRESS, emailValidator);
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

    interface TypeValidator {
        public void validate(Object value, Errors errors, GlobalSettingType globalSetting);
    }
}

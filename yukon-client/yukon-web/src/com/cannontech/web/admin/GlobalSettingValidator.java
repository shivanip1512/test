package com.cannontech.web.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
            public void validate(Object value, Errors errors) {
                String httpProxy = (String) value;
                YukonValidationUtils.checkIsBlankOrExceedsMaxLength(errors, "values[HTTP_PROXY]", httpProxy, false, 1000);

                if (!StringUtils.isBlank(httpProxy)
                        && !httpProxy.equals("none")
                        && !urlWithPortMatcher.matcher(httpProxy).matches()) {
                    errors.rejectValue("values[HTTP_PROXY]", baseKey + "invalidProxy", null,"");
                }
            }
        });
        validators.put(GlobalSettingType.SYSTEM_TIMEZONE, new TypeValidator() {
            @Override
            public void validate(Object value, Errors errors) {
                String timeZoneId = (String) value;
                YukonValidationUtils.checkExceedsMaxLength(errors, "values[SYSTEM_TIMEZONE]", timeZoneId, 1000);
                if (StringUtils.isNotBlank(timeZoneId)) {
                    try {
                        TimeZone timeZone=CtiUtilities.getValidTimeZone(timeZoneId);
                        DateTimeZone.forTimeZone(timeZone);
                    } catch(BadConfigurationException e) {
                        errors.rejectValue("values[SYSTEM_TIMEZONE]", baseKey + "invalidTimeZone", null,"");
                    } catch (IllegalArgumentException e){
                        errors.rejectValue("values[SYSTEM_TIMEZONE]", baseKey + "invalidJodaTimeZone",null,"");
                    }
                }
            }
        });
        validators.put(GlobalSettingType.ECOBEE_SERVER_URL, new TypeValidator() {
            private final String[] schemes = {"http", "https"};
            @Override
            public void validate(Object value, Errors errors) {
                String ecobeeUrl = (String) value;

                UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
                if (!urlValidator.isValid(ecobeeUrl)) {
                    errors.rejectValue("values[ECOBEE_SERVER_URL]", baseKey + "invalidURL");
                }
            }
        });
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
                validators.get(globalSettingType).validate(values.get(globalSettingType), errors);
            }
        }
    }

    interface TypeValidator {
        public void validate(Object value, Errors errors);
    }
}

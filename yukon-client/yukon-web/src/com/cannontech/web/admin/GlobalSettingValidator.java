package com.cannontech.web.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.admin.YukonConfigurationController.GlobalSettingsEditorBean;
import com.cannontech.common.exception.BadConfigurationException;

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
                        CtiUtilities.getValidTimeZone(timeZoneId);
                    } catch(BadConfigurationException e) {
                        errors.rejectValue("values[SYSTEM_TIMEZONE]", baseKey + "invalidTimeZone", null,"");
                    }
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

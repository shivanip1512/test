package com.cannontech.system;

import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.joda.time.DateTimeZone;
import org.springframework.validation.Errors;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.YukonValidationUtils;

public class GlobalSettingTypeValidators {
    private static String baseKey = "yukon.web.modules.adminSetup.config.error.";
    
    public static TypeValidator urlValidator = new TypeValidator() {
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

    public static TypeValidator emailValidator = new TypeValidator() {
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

    public static TypeValidator ipHostNameValidator = new TypeValidator() {
        Pattern ipHostNameMatcher =
            Pattern.compile("^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$");

        @Override
        public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
            String ipHost = (String) value;
            if (StringUtils.isNotBlank(ipHost)) {
                // AD_SERVER_ADDRESS supports  a space separated list of addresses
                if (globalSettingType == GlobalSettingType.AD_SERVER_ADDRESS) {
                    String[] hosts = StringUtils.split(ipHost, " ");
                    for (String host : hosts) {
                        if (!ipHostNameMatcher.matcher(host).matches()) {
                            errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidIPHostName");
                        }
                    }
                } else {
                    if (!ipHostNameMatcher.matcher(ipHost).matches()) {
                        errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidIPHostName");
                    }
                }
            }
        }
    };    

    public static TypeValidator portValidator = new TypeValidator() {
        @Override
        public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
            Pattern portMatcher = Pattern.compile("^[0-9]+$");
            String port = (String) value;
            if (StringUtils.isNotBlank(port)) {
                String[] ports = StringUtils.split(port, " ");
                for (String prt : ports) {
                    if (!portMatcher.matcher(prt).matches()) {
                        errors.rejectValue("values[" + globalSettingType + "]", "typeMismatch");
                    }
                }
            }
        }
    };

    public static TypeValidator urlWithPortValidator = new TypeValidator() {
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
    };

    public static TypeValidator timezoneValidator = new TypeValidator() {
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
    };
    
    public static TypeValidator integerRangeValidator = new TypeValidator() {
        @Override
        public void validate(Object value, Errors errors, GlobalSettingType globalSettingType) {
            if (globalSettingType.getValidationValue() != null) {
                String field = "values[" + globalSettingType + "]";
                try {
                    Range<Integer> range = (Range<Integer>)globalSettingType.getValidationValue();
                    Integer valueInt = Integer.class.cast(value);
                    if (value != null) {
                        YukonValidationUtils.checkRange(errors, field, valueInt, range, true);
                    } else {
                        errors.rejectValue(field, "yukon.web.error.outOfRangeObject", 
                                           new Object[] { range.isIncludesMinValue() ? 1 : 0, range.getMin(),  
                                                          range.isIncludesMaxValue() ? 1 : 0, range.getMax() },
                                                          "Must be " + range.toString() + ".");
                    }
                } catch (ClassCastException e) {
                    errors.rejectValue(field, "typeMismatch");
                }
            }
        }
    };
    
    public interface TypeValidator {
        public void validate(Object value, Errors errors, GlobalSettingType globalSetting);
    }
}

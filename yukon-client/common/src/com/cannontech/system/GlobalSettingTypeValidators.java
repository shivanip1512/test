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
    
    public static TypeValidator<String> urlValidator = new TypeValidator<>() {
        private final String[] schemes = { "http", "https" };

        @Override
        public void validate(String url, Errors errors, GlobalSettingType globalSettingType) {
            if (StringUtils.isNotBlank(url)) {
                UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
                if (!urlValidator.isValid(url)) {
                    errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidURL");
                }
            }
        }
    };

    public static TypeValidator<String> emailValidator = new TypeValidator<>() {
        @Override
        public void validate(String email, Errors errors, GlobalSettingType globalSettingType) {
            if (StringUtils.isNotBlank(email)) {
                boolean emailValid = EmailValidator.getInstance().isValid(email);
                if (!emailValid) {
                    errors.rejectValue("values[" + globalSettingType + "]", baseKey + "invalidEmail");
                }
            }
        }
    };

    public static TypeValidator<String> ipHostNameValidator = new TypeValidator<>() {
        Pattern ipHostNameMatcher =
            Pattern.compile("^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$");

        @Override
        public void validate(String ipHost, Errors errors, GlobalSettingType globalSettingType) {
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

    /**
     * Validate individual Integer port 
     */
    public static TypeValidator<Integer> portValidator = new TypeValidator<>() {
        @Override
        public void validate(Integer port, Errors errors, GlobalSettingType globalSettingType) {
            if (port != null) {
                String field = "values[" + globalSettingType + "]";
                Range<Integer> range = Range.inclusive(0, 65535);
                YukonValidationUtils.checkRange(errors, field, port, range, true);
            }
        }
    };
    
    /**
     * Validates space separated String of numeric ports 
     */
    public static TypeValidator<String> portsValidator = new TypeValidator<>() {
        @Override
        public void validate(String ports, Errors errors, GlobalSettingType globalSettingType) {
            Pattern portMatcher = Pattern.compile("^[0-9]+$");
            String field = "values[" + globalSettingType + "]";
            if (StringUtils.isNotBlank(ports)) {
                String[] portsArray = StringUtils.split(ports, " ");
                for (String port : portsArray) {
                    if (!portMatcher.matcher(port).matches()) {
                        errors.rejectValue(field, "typeMismatch");
                    }
                    // validate each prt separately.
                    portValidator.validate(Integer.valueOf(port), errors, globalSettingType);
                }
            }
        }
    };


    public static TypeValidator<String> urlWithPortValidator = new TypeValidator<>() {
        Pattern urlWithPortMatcher = Pattern.compile("\\s*(.*?):(\\d+)\\s*");
        @Override
        public void validate(String urlWithPort, Errors errors, GlobalSettingType globalSettingType) {
            YukonValidationUtils.checkIsBlankOrExceedsMaxLength(errors, "values[" + globalSettingType + "]", urlWithPort, true, 1000);

            if (!StringUtils.isBlank(urlWithPort)
                    && !urlWithPort.equals("none")
                    && !urlWithPortMatcher.matcher(urlWithPort).matches()) {
                errors.rejectValue("values["+globalSettingType+"]", baseKey + "invalidProxy", null,"");
            }
        }
    };

    public static TypeValidator<String> timezoneValidator = new TypeValidator<>() {
        @Override
        public void validate(String timeZoneId, Errors errors, GlobalSettingType globalSettingType) {
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
    
    public static TypeValidator<Integer> integerRangeValidator = new TypeValidator<>() {
        @Override
        public void validate(Integer value, Errors errors, GlobalSettingType globalSettingType) {
            if (globalSettingType.getValidationValue() != null) {
                String field = "values[" + globalSettingType + "]";
                Range<Integer> range = (Range<Integer>)globalSettingType.getValidationValue();
                YukonValidationUtils.checkRange(errors, field, value, range, true);
            }
        }
    };
    
    public interface TypeValidator<T> {
        public void validate(T value, Errors errors, GlobalSettingType globalSetting);
    }
}